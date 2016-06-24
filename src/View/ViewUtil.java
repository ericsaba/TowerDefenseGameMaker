package View;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;


public class ViewUtil {
    public static Node bindCursor (Node node,
                                   Node pane,
                                   Point2D initial,
                                   KeyCode key,
                                   BooleanProperty unclicked) {
        final Group wrapGroup = new Group(node);
        wrapGroup.relocate(initial.getX(), initial.getY());
            pane.setOnMouseMoved(mouseEvent -> {
                // mouseEvent.consume();
                Point2D current = getMouseLocation(mouseEvent, node);
                wrapGroup.relocate(current.getX(), current.getY());
            });
        pane.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == key) {
                unclicked.setValue(false);
                unbindCursor(pane, node);
            }
        });

        // TODO: figure out why keyPressed caller must be scene, and not pane or node
        // EDIT: I changed this to node and it worked perfectly for me!

        return wrapGroup;
    }

    /**
     * Binds the cursor to the node. Binding will be disabled, and the node will be removed from the
     * scene, when the appropriate key is pressed.
     *
     * @param node Node to be bound
     * @param pane Scene of the node
     * @param initial Initial position
     * @param key Disabling key
     * @return
     */
    public static Node bindCursor (Node node,
                                   Node pane,
                                   Point2D initial,
                                   KeyCode key,
                                   boolean scene) {
        final Group wrapGroup = new Group(node);
        wrapGroup.relocate(initial.getX(), initial.getY());
        if (scene) {
            pane.getScene().setOnMouseMoved(mouseEvent -> {
                // mouseEvent.consume();
                Point2D current = getMouseLocation(mouseEvent, node);
                wrapGroup.relocate(current.getX(), current.getY());
            });
        }
        else {
            pane.setOnMouseMoved(mouseEvent -> {
                // mouseEvent.consume();
                Point2D current = getMouseLocation(mouseEvent, node);
                wrapGroup.relocate(current.getX(), current.getY());
            });
        }
        pane.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == key) {
                unbindCursor(pane, node);
            }
        });

        // TODO: figure out why keyPressed caller must be scene, and not pane or node
        // EDIT: I changed this to node and it worked perfectly for me!

        return wrapGroup;
    }

    public static void unbindCursor (Node pane, Node node) {
        pane.getScene().setOnMouseMoved(null);
        pane.getScene().setOnKeyPressed(null);
        node.setVisible(false);
        try {
            ((Group) node.getParent()).getChildren().remove(node);
        }
        catch (NullPointerException e) {

        }
    }

    public static void addMouseMovementHandler () {

    }

    /**
     * Determines the normalized ratio (0 to 1) of width and height within a parent
     * container from local pixel coordinates
     *
     * @param mouseEvent Triggering MouseEvent
     * @param parent Container
     * @return
     */
    public static Point2D normalizePixels (MouseEvent mouseEvent, Parent parent) {
        return normalizePixels(mouseEvent.getX(), mouseEvent.getY(), parent);
    }

    /**
     * Determines the normalized ratio (0 to 1) of width and height within a parent
     * container from local pixel coordinates
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param parent Container
     * @return
     */
    public static Point2D normalizePixels (double x, double y, Parent parent) {
        return new Point2D(x / parent.getBoundsInLocal().getWidth(),
                           y / parent.getBoundsInLocal().getHeight());
    }

    /**
     * Returns the local (parent-wide) mouseLocation triggered by a MouseEvent
     * on a node. Is used for cursor binding.
     *
     * @param mouseEvent MouseEvent
     * @param node Node
     * @return
     */
    public static Point2D getMouseLocation (MouseEvent mouseEvent, Node node) {
        return new Point2D(mouseEvent.getX() + getCenterOffsetX(node),
                           mouseEvent.getY() + getCenterOffsetY(node));
    }

    /**
     * Returns the global (scene-wide) mouseLocation triggered by a MouseEvent
     * on a node. Is used for cursor binding.
     *
     * @param mouseEvent MouseEvent
     * @param node Node
     * @return
     */
    public static Point2D getMouseSceneLoc (MouseEvent mouseEvent, Node node) {
        return new Point2D(mouseEvent.getSceneX() + getCenterOffsetX(node),
                           mouseEvent.getSceneY() + getCenterOffsetY(node));
    }

    /**
     * Used to find the center of a node. Gets the X offset.
     *
     * @param node Node
     * @return
     */
    public static double getCenterOffsetX (Node node) {
        return (node != null) ? -node.getBoundsInLocal().getWidth() / 2 : 0.0;
        // return opt.map(nd -> -nd.getBoundsInLocal().getWidth() / 2).orElse(0.0);
    }

    /**
     * Used to find the center of a node. Gets the X offset.
     *
     * @param node Node
     * @return
     */
    public static double getCenterOffsetY (Node node) {
        return (node != null) ? -node.getBoundsInLocal().getHeight() / 2 : 0.0;
        // return opt.map(nd -> -node.getBoundsInLocal().getHeight() / 2).orElse(0.0);
    }
}
