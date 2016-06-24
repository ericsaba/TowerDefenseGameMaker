package gae.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import engine.fieldsetting.Settable;
import engine.game.Player;
import gae.editor.EditingParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PlayerBuilder implements Builder {

    private static final String SET_WALLET_METHOD = "setWalletUnit";
    private VBox builder;
    private Button createButton;
    private EditingParser parser;
    private List<Method> settables;
    private List<FieldMaker> fields;
    private List<String> sublabels;
    private List<String> allInputs;
    private PlayerData playerData;

    private HBox walletBox;
    private ComboBox<String> walletDropDown;

    @SuppressWarnings("static-access")
    public PlayerBuilder () {
        builder = new VBox(15);
        createButton = new Button("CREATE");
        parser = new EditingParser();
        settables = parser.getMethodsWithAnnotation(Player.class, Settable.class);
        fields = new ArrayList<>();
        sublabels = new ArrayList<>();
        allInputs = new ArrayList<>();
        playerData = new PlayerData(allInputs);
        walletBox = new HBox(15);
        walletDropDown = new ComboBox<>();
        populateFields(settables);
        createWalletDropDown();
        builder.getChildren().add(walletBox);
        builder.getChildren().add(createButton);
        setUpCreateButton();
    }
    
    @Override
    public Pane getBuilder () {
        return builder;
    }

    @Override
    public void build () {
        fields.forEach(e -> {
            allInputs.add(e.getInput().getText());
        });
        allInputs.add(walletDropDown.getSelectionModel().getSelectedItem());
        playerData.fillProperties();
    }
    
    
    @Override
    public BuildObjectData getData () {
        return playerData;
    }

    /**
     * Creates field with all setters needed. Uses recursion to check if parameters of the method
     * need to be built as well.
     */
    @SuppressWarnings("static-access")
    private void populateFields (List<Method> methods) {
        methods.forEach(e -> {
            Arrays.asList(e.getParameterTypes())
                    .forEach(f -> {
                                 if (f.isPrimitive() || f.equals(String.class)) {
                                     if (e.getName().equals(SET_WALLET_METHOD)) {
                                         
                                     }
                                     else {
                                         FieldMaker fm = new FieldMaker(e);
                                         fields.add(fm);
                                         builder.getChildren().add(fm.getField());
                                     }
                                 }
                                 else {
                                     addSubLabel(e);
                                     try {
                                         populateFields(parser.getMethodsWithAnnotation(Class
                                                 .forName(f.getName()), Settable.class));
                                     }
                                     catch (Exception e1) {
                                         e1.printStackTrace();
                                     }
                                 }
                             });
        });
    }

    /**
     * creates combobox that observes the labels that have been put in so far
     */
    private void createWalletDropDown () {
        Label l = new Label("WalletUnit");
        ObservableList<String> observableList = FXCollections.observableArrayList(sublabels);
        walletDropDown.setItems(observableList);
        walletBox.getChildren().addAll(l, walletDropDown);
    }

    /**
     * adds sub label by extracing method's property name if its parameter are NOT Strings or
     * primitives
     * 
     * @param e
     */
    private void addSubLabel (Method e) {
        Text sublabel = new Text(e.getName().substring(e.getName().indexOf("set") + "set".length()));
        sublabels.add(sublabel.getText());
        builder.getChildren().add(sublabel);
    }

    /**
     * when create button is clicked, all inputed data is sent to PlayerData
     */
    private void setUpCreateButton () {
        createButton.setAlignment(Pos.CENTER);
        createButton.setOnMouseClicked(e -> {
            build();
        });
    }
}
