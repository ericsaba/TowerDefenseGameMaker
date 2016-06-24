package gae.editor;

import javafx.scene.control.TextField;


/**
 * Represents an editor based on input text from the author. Could be used for features such as
 * object name.
 *
 * @author Brandon Choi
 *
 */

public class TextEditor extends ComponentEditor {

    private TextField field;

    public TextEditor () {
        super();
        field = new TextField();
        getEditBox().getChildren().addAll(getLabel(), field);
    }

    @Override
    public void clear () {
        field.clear();
    }

    @Override
    public void defaultField () {
        field.setText("*DEFAULT*");
    }

    @Override
    public Object createObject (Class<?> c) {
        return field.getText();
    }
}
