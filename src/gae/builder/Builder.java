package gae.builder;

import javafx.scene.layout.Pane;

/**
 * Interface for all builders such as the button builder or terrain builder to implement. Differs
 * from an Editor because Builders will build things that are NOT GameObjects.
 *
 * @author Brandon Choi
 *
 */

public interface Builder {

    /**
     * returns the outermost Node in order to grab the Builder after it has been created. This Node
     * can then be added to the UI for the user to work with.
     *
     * @return
     */
    Pane getBuilder ();

    /**
     * function to be called when create button is pressed
     */
    void build ();

    /**
     * retrieves the respective BuildObjectData
     * 
     * @return
     */
    BuildObjectData getData ();
}
