package gae.gameView;

import engine.gameobject.GameObject;
import engine.gameobject.Graphic;
import engine.gameobject.Purchasable;
import gae.backend.Placeable;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * Creates a checklist item that holds a placeable to be added to a ShopCheckList
 * 
 * @author Nina Sun
 */
public class ShopCheckListItem implements CheckListItem {
    private Placeable placeable;
    private CheckBox checkbox;

    public ShopCheckListItem (Placeable obj) {
        placeable = obj;
        checkbox = new CheckBox();
    }

    /**
     * Returns the node to be added to the checklist, showing a thumbnail, name and checkbox
     * 
     * @author Nina Sun
     */
    public Node getNode () {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        Graphic graphic = placeable.getGraphic().clone();
        graphic.setHeight(50);
        Node image = graphic.getResizedGraphic(1);
        Label label = new Label(placeable.getName());
        Text description = new Text(placeable.getDescription());
        System.out.println(placeable.getWeapon().getValue());
        String value;
        try {
            value = Double.toString(placeable.getWeapon().getValue());
        }
        catch (Exception e) {
            value = "No weapon value found";
        }
        Text price = new Text("Price: " + value);
        VBox vbox = new VBox(label, description, price);
        hbox.getChildren().addAll(image, vbox, checkbox);
        return hbox;
    }

    public BooleanProperty getSelectedProperty () {
        return checkbox.selectedProperty();
    }

    /**
     * Returns placeable that the item holds
     * 
     * @return list
     */
    public Placeable getPlaceable () {
        return placeable;
    }

}
