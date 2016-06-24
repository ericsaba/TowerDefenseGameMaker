package gae.tabView;

import engine.fieldsetting.Settable;
import engine.game.Game;
import engine.game.Level;
import engine.game.Player;
import engine.game.StoryBoard;
import engine.shop.ShopModel;
import gae.editor.EditingParser;
import gae.gameView.InteractionTable;
import gae.gameWorld.FixedGameWorldFactory;
import gae.gameWorld.FreeGameWorldFactory;
import gae.gameWorld.GameWorldFactory;
import gae.gridView.LevelView;
import gae.levelPreferences.LevelPreferencesEditor;
import gae.listView.LibraryData;
import gae.openingView.UIObject;
import gae.waveeditor.WaveEditor;
import gameworld.AbstractWorld;
import gameworld.FreeWorld;
import gameworld.GameWorld;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import voogasalad.util.pathsearch.graph.GridCell;


/**
 * Central container for the central tab view in the gae editor
 * 
 * @author JohnGilhuly
 *
 */

public class CentralTabView implements UIObject {
    private VBox baseNode;
    private TabPane tabView;
    private int levelCount;
    private Scene scene;
    private ITab hudTab;
    private ITab shopTab;
    private ITab gameObjectTab;
    private ITab playerTab;
    private LevelView levelView;
    private LibraryData libraryData;
    private Game game;
    private Player player;
    private GameWorldFactory gameWorldFactory;
    private boolean editorInstantiated;
    private FreeWorld freeworld;
    private BooleanProperty isFreeWorld = new SimpleBooleanProperty();
    private ShopModel shopModel;
    private boolean shopSet;
    private AbstractWorld nextWorld;

    public CentralTabView (Scene sceneIn, Game gameIn, String gameTypeIn) {
        scene = sceneIn;
        game = gameIn;
        initialize(gameTypeIn);
    }

    private void initialize (String gameTypeIn) {
        libraryData = LibraryData.getInstance();
        levelCount = 1;
        shopSet = false;

        baseNode = new VBox();
        tabView = new TabPane();
        // refactor this code
        shopTab = new ShopTab();
        hudTab = new HudEditorTab(null);
        gameObjectTab = new GameObjectEditorTab(scene, getConsumer(), getBiconsumer());
        playerTab = new PlayerTab();

        tabView.getTabs().addAll(shopTab.getBaseTabNode(), hudTab.getBaseTabNode(),
                                 playerTab.getBaseTabNode());

        Button newLevel = new Button("Add Level");
        newLevel.setOnAction(e -> {
            if (!editorInstantiated) {
                GameObjectEditorTab gameObjectTab = new GameObjectEditorTab(scene, getConsumer(),
                                                                            getBiconsumer());
                tabView.getTabs().add(gameObjectTab.getBaseTabNode());
                editorInstantiated = true;
            }
            createNewLevel();
        });

        baseNode.getChildren().addAll(newLevel, tabView);
        gameWorldFactory = createGameWorldFactory(gameTypeIn);

        try {
            setUpShopAndLinkToGame();
            setUpPlayerAndLinkToGame();
        }
        catch (ClassNotFoundException |
                IllegalAccessException |
                IllegalArgumentException |
                InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Creates the GameWorldFactory that will be used for creating GameWorlds later on
     * 
     * @param gameTypeIn
     * @return
     */
    private GameWorldFactory createGameWorldFactory (String gameTypeIn) {
        if (gameTypeIn != null && gameTypeIn.equals("Free Path")) {
            return new FreeGameWorldFactory();
        }
        else {
            return new FixedGameWorldFactory();
        }
    }

    /**
     * Uses reflection to set up the ShopModel and link it to game
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private void setUpShopAndLinkToGame ()
                                          throws ClassNotFoundException, IllegalAccessException,
                                          IllegalArgumentException,
                                          InvocationTargetException {

        shopModel = ((ShopTab) shopTab).getShop();

        for (Method m : EditingParser.getMethodsWithAnnotation(Class.forName(game.getClass()
                .getName()), Settable.class)) {
            if (m.getName().equals("setShop")) {
                m.invoke(game, shopModel);
            }
        }
    }

    /**
     * Uses reflection to set up the Player and link it to game
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private void setUpPlayerAndLinkToGame ()
                                            throws ClassNotFoundException, IllegalAccessException,
                                            IllegalArgumentException,
                                            InvocationTargetException {

        player = ((PlayerTab) playerTab).getPlayer();

        for (Method m : EditingParser.getMethodsWithAnnotation(Class.forName(game.getClass()
                .getName()), Settable.class)) {
            if (m.getName().equals("setPlayer")) {
                m.invoke(game, player);
            }
        }
    }

    /**
     * Creates a new level using reflection and sets all of the necessary dependencies
     */
    private void createNewLevel () {
        isFreeWorld.set(false);
        levelView = new LevelView(setSpawnPoints(), setWalkable(), isFreeWorld);
        Pane levelViewPane = levelView.getBorder(scene);
        gameWorldFactory.bindGridSize(levelView.getGridDimensionProperty());
        nextWorld = gameWorldFactory.createGameWorld();
        if (nextWorld instanceof FreeWorld) {
            freeworld = (FreeWorld) nextWorld;
            LibraryData.getInstance().addFreeWorldPath(freeworld.getPath());
            isFreeWorld.set(true);
        }

        LevelPreferencesEditor prefs = new LevelPreferencesEditor();
        WaveEditor waves = createLevelAndWaveObject(nextWorld, prefs);
        InteractionTable iTable = new InteractionTable();

        nextWorld.setCollisionEngine(iTable.getData().getCollisionEngine());
        nextWorld.setRangeEngine(iTable.getData().getRangeEngine());
        LevelTabSet newLevel = new LevelTabSet(levelViewPane, waves.getObject(), iTable.getTable(),
                                               prefs.getObject());

        Tab newTab = new Tab("Level:" + levelCount++);
        newTab.setContent(newLevel.getBaseNode());
        newTab.setClosable(false);
        tabView.getTabs().add(newTab);
        ((HudEditorTab) hudTab).setBackgroundImage(new Image(levelView.getBackgroundImagePath()
                .getValue()));
    }

    /**
     * Creates a level and wave object and links them
     * 
     * @param nextWorld
     * @param prefs
     * @return
     */
    private WaveEditor createLevelAndWaveObject (GameWorld nextWorld, LevelPreferencesEditor prefs) {
        Level levelData = null;
        StoryBoard sb = new StoryBoard();
        List<Method> levelMethods;

        try {
            levelData = (Level) Class
                    .forName(EditingParser
                            .getInterfaceClasses("engine.fieldsetting.implementing_classes")
                            .get("Level").get(0)).newInstance();

            levelMethods = EditingParser.getMethodsWithAnnotation(Class.forName(levelData
                    .getClass().getName()), Settable.class);

            for (Method m : levelMethods) {
                checkAndInvokeMethods(nextWorld, levelData, sb, m);
            }

            if (!shopSet) {
                for (Method m : EditingParser.getMethodsWithAnnotation(Class.forName(shopModel
                        .getClass().getName()), Settable.class)) {
                    if (m.getName().equals("setGameWorld")) {
                        m.invoke(shopModel, nextWorld);
                        shopSet = true;
                    }
                }
            }
        }
        catch (InstantiationException |
                IllegalAccessException |
                InvocationTargetException |
                ClassNotFoundException e) {
            e.printStackTrace();
        }

        prefs.setLevel(levelData);
        levelView.setLevel(levelData);
        game.getLevelBoard().addLevel(levelData);
        return new WaveEditor(sb, levelData.getGameWorld());
    }

    /**
     * Checks specific methods and invokes them if necessary
     * 
     * @param nextWorld
     * @param levelData
     * @param sb
     * @param m
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void checkAndInvokeMethods (GameWorld nextWorld,
                                        Level levelData,
                                        StoryBoard sb,
                                        Method m)
                                                 throws IllegalAccessException,
                                                 InvocationTargetException {
        if (m.getName().equals("setStoryBoard")) {
            m.invoke(levelData, sb);
        }
        if (m.getName().equals("setGameWorld")) {
            m.invoke(levelData, nextWorld);
        }
    }

    @Override
    public Node getObject () {
        return baseNode;
    }

    /**
     * Returns a consumer
     * 
     * @return
     */
    public Consumer<Object> getConsumer () {
        return e -> libraryData.addGameObjectToList(e);
    }

    /**
     * Returns a biconsumer
     * 
     * @return
     */
    public BiConsumer<Class<?>, Object> getBiconsumer () {
        BiConsumer<Class<?>, Object> biConsumer = (klass, o) -> {
            libraryData.addCreatedObjectToList(klass, o);
        };
        return biConsumer;
    }

    public BiConsumer<List<GridCell>, List<GridCell>> setWalkable () {
        return (tower, enemy) -> { 
            nextWorld.setObstacles(enemy);
            nextWorld.setTowerObstacles(tower);
        };
    }

    public BiConsumer<List<GridCell>, List<GridCell>> setSpawnPoints () {
        return (start, end) -> {
            freeworld.setSpawnPoints(start);
            freeworld.setEndPoints(end);
        };
    }
}
