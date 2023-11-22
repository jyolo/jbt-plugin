package ideActions;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;


public class ChangeDocumentListener implements DocumentListener {
    @Override
    public void documentChanged(DocumentEvent event) {
//        Document document = event.getDocument();
//        System.out.println("documentChanged");
        // 编辑后有定时器的则清除定时器，直到保存再次保存才从新触发定时器
//        if(SaveDocumentListener.timerMap.containsKey(document)){
//            SaveDocumentListener.timerMap.get(document).cancel();
//            SaveDocumentListener.timerMap.remove(document);
//            System.out.println("定时器已存在，文档被编辑，清除定时器");
//        }
    }

}
