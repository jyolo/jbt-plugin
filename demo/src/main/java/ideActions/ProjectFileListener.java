package ideActions;

import com.common.util;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import org.jetbrains.annotations.NotNull;


public class ProjectFileListener extends VirtualFileAdapter {
    VirtualFile triggerFile;
    Project project;
    public ProjectFileListener(VirtualFile triggerFile,Project project){
        this.triggerFile = triggerFile;
        this.project = project;
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        if (!event.getFile().equals(triggerFile)) {
            return;
        }
        // 获取当前编辑的文件
        Document currentDocument = util.getSelectedDocument(project);
        System.out.println("triggerFile:" + triggerFile.toString() + " changed, currentDocument: " + currentDocument);

        if(currentDocument != null && SaveDocumentListener.timerMap.containsKey(currentDocument)){
            SaveDocumentListener.timerMap.get(currentDocument).cancel();
            SaveDocumentListener.timerMap.remove(currentDocument);
            System.out.println("定时器已存在，文档被关闭，清除定时器");
        }
    }
}
