package gae.editor;

import gae.editorView.EditorIntermediateView;
import java.util.ArrayList;
import java.util.Map;


/**
 * 
 * @author Eric Saba
 * 
 *         A static intermediate class that decides whether to show an EditorIntermediateView, a
 *         PopUpEditorView, or no editor at
 *         all, based on the number of concrete classes that implement each interface.
 *
 */
public class EditorIntermediate {

    public static void handleEditorPop (ObjectComponentEditor componentEditor, int i) {
        Map<String, ArrayList<String>> map =
                EditingParser.getInterfaceClasses("engine.fieldsetting.implementing_classes");
        ArrayList<String> array = map.get(componentEditor.getInterfaceClass().getName());
        if (array == null)
            componentEditor.popNewEditor(i);
        else if (array.size() < 2) {
            componentEditor.popNewEditor(i);
        }
        else
            new EditorIntermediateView(array, componentEditor, i);
    }
}
