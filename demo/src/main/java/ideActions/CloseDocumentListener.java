package ideActions;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.qianliuAiUi.EditorGutterIconService;
import com.qianliuAiUi.JsBridgeService;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CloseDocumentListener implements FileEditorManagerListener {
    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        System.out.println("-----------fileClosed " + file.getPath());
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if(SaveDocumentListener.timerMap.containsKey(document)){
            SaveDocumentListener.timerMap.get(document).cancel();
            SaveDocumentListener.timerMap.remove(document);
            System.out.println("定时器已存在，文档被关闭，清除定时器");
        }

        Map<String, String> params = new HashMap<>();
        params.put("action", "editor.closeFile");
        params.put("filePath", file.getPath());
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        // 设置当前文件为关闭状态
        EditorGutterIconService.getInstance(project).setSelectFileColse(file.getPath(), true);

        new JsBridgeService(project).callActionFromIde("editor.closeFile", params);
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
//        System.out.println("-----------selectionChanged");
        // 切换文本后监听 选中代码
        ApplicationManager.getApplication().invokeLater(() -> {
            try{
                Project project = event.getManager().getProject();
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                if(editor !=null ){
                    editor.getSelectionModel().addSelectionListener(new SelectDocumentListener(project));
                }
            }catch (Exception e){
                System.out.println("selectionChanged --------- error: " + e.getMessage());
            }

        });
    }

}
