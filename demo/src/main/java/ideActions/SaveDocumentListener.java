package ideActions;

import com.common.util;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import browse.JsBridgeService;
import settings.AiReviewSettingsState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

public class SaveDocumentListener implements FileDocumentManagerListener {

    static Map<Document, Timer> timerMap = new HashMap<>();
    AiReviewSettingsState settingsState = AiReviewSettingsState.getInstance();

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        System.out.println("自动review "+ settingsState.enableAiReview);
        if (settingsState.enableAiReview == false){
            System.out.println("自动review 已关闭，不触发");
            return;
        }
        Document currentDocument = null;
        // 获取当前的project
        Project project = util.getCurrentProjectFromDocument(document);
        if (project != null) {
            // 获取当前编辑的文件
            currentDocument = util.getSelectedDocument(project);
        }

        if (currentDocument == null || !document.equals(currentDocument)){
            return;
        }

        sendSaveEventToWebView(currentDocument, project);

//        try {
//            String projectDir = project.getBasePath();
//            System.out.println("Project directory: " + projectDir);
//            ProcessBuilder builder = new ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD");
//            builder.directory(new File(projectDir));
//            Process process = builder.start();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String branchName = reader.readLine();
//            System.out.println("Current branch name: " + branchName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public void sendSaveEventToWebView(Document currentDocument, Project project){
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile virtualFile = fileEditorManager.getSelectedFiles()[0];
        String filePath = virtualFile.getPath();
        String language = util.getLanguageByFilePath(filePath);
        Map<String, Object> params = new HashMap<>();
        // 组装所需 js 端 所需的数据
        params.put("code", currentDocument.getText());
        params.put("language", language);
        params.put("filePath", filePath);
        params.put("confAutoDealyTime", AiReviewSettingsState.getInstance().delayTime);

        new JsBridgeService(project).callActionFromIde("editor.saveFile", params);

//        // 带定时器的逻辑
//        Integer delayTime = settingsState.delayTime;
//        // 已经触发过自动reveiw的定时器，再次保存则停止之前定时器，并重新开始创建定时器
//        if(!timerMap.isEmpty() && timerMap.containsKey(currentDocument)) {
//            timerMap.get(currentDocument).cancel();
//            timerMap.remove(currentDocument);
//            System.out.println("reveiw 定时器 已经存在 文档： " + currentDocument.toString() + "取消之前的定时器");
//        }
//        System.out.println("开始自动 reveiw 定时器 文档：" + currentDocument.toString());
//        // Document 创建定时器
//        timerMap.put(currentDocument, new Timer());
//        timerMap.get(currentDocument).schedule(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    // 当前ide选中的 Document
//                    Document selectDocument = null;
//                    Project project = util.getCurrentProjectFromDocument(currentDocument);
//                    if (project !=null) {
//                        // 获取当前编辑的文件
//                        selectDocument = util.getSelectedDocument(project);
//                    }
//                    // 最新当前编辑的 Document 不是本次事件中的 currentDocument 则不触发事件
//                    if (!selectDocument.equals(currentDocument)){
//                        System.out.println("当前编辑的文档："+selectDocument+"不是，本次事件中的文档:"+currentDocument+"不触发自动reveiw");
//                        return;
//                    }
//                    // 再检查一次开关，如何关闭了就不触发了
//                    if (settingsState.enableAiReview == false){
//                        System.out.println("自动review 已关闭，不触发");
//                        return;
//                    }
//                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
//                    VirtualFile virtualFile = fileEditorManager.getSelectedFiles()[0];
//                    String filePath = virtualFile.getPath();
//                    String language = util.getLanguageByFilePath(filePath);
//                    Map<String, String> params = new HashMap<>();
//                    // 组装所需 js 端 所需的数据
//                    params.put("code", currentDocument.getText());
//                    params.put("language", language);
//                    params.put("file_path", filePath);
//                    System.out.println("定时器触发自动reveiw" + currentDocument.toString() +" 参数： "+ params.toString());
//
//                    new JsBridgeService(project).callActionFromIde("editor.triggerAutoReview", params);
//
//                }catch (Exception e){
//                    System.out.println(e.getMessage());
//                    e.printStackTrace();
//                }
//
//                if (timerMap.get(currentDocument) != null){
//                    // cancel timer
//                    timerMap.get(currentDocument).cancel();
//                    timerMap.remove(currentDocument);
//                }
//
//            };
//        }, delayTime);

    }

}