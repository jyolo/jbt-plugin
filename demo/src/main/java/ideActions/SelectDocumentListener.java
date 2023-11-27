package ideActions;


import com.common.util;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import settings.OpenAISettingsState;
import browse.JsBridgeService;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;


public class SelectDocumentListener implements SelectionListener {
    private static final String ACTION_ADD_TESTS = "add test";
    private static final String ACTION_EXPLAIN = "explain";
    private static final String ACTION_CONVERT = "convert";
    private static final String ACTION_AI_REVIEW = "review";
    Project project;

    public SelectDocumentListener(Project project){
        this.project = project;
    }
    @Override
    public void selectionChanged(@NotNull SelectionEvent event) {
        // 处理选择文本的行为
        String selectedText = event.getEditor().getSelectionModel().getSelectedText();
        if (selectedText == null){ return; }

//        Map<String, String> params = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile virtualFile = fileEditorManager.getSelectedFiles()[0];
        String filePath = virtualFile.getPath();
        String language = util.getLanguageByFilePath(filePath);
        params.put("code", selectedText);
        params.put("language", language);
        params.put("filePath", filePath);

        try{
            addSelectTextToolTips(event);
        }catch (Exception e){
            System.out.println("触发选中悬浮提示框失败：" + e.toString());
        }

        OpenAISettingsState setting = OpenAISettingsState.getInstance();
        Map<String, String> customInstructionMap = new HashMap<>();
//        customInstructionMap.put(ACTION_OPTIMIZE, setting.optimizeInstructions);
        customInstructionMap.put(ACTION_ADD_TESTS, setting.addTestsInstructions);
        customInstructionMap.put(ACTION_EXPLAIN, setting.explainInstructions);
        customInstructionMap.put(ACTION_CONVERT, setting.languageConvertInstructions);
        customInstructionMap.put(ACTION_AI_REVIEW, setting.AICodeReviewInstructions);
        params.put("customInstructionMap", customInstructionMap);

        new JsBridgeService(project).callActionFromIde("editor.selectCode", params);

    }

    public void addSelectTextToolTips(SelectionEvent event){
        Editor editor = event.getEditor();
        if (timer != null) {
            timer.cancel();
            System.out.println("定时器已存在");
            timer = null;
        }

        // 创建定时器
        timer = new Timer();
        // 创建定时任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timer = null;
                SwingUtilities.invokeLater(() -> {
                    // 在这里编写需要延迟执行的任务
                    String text = editor.getSelectionModel().getSelectedText();
                    if(text == null){
                        return;
                    }
                    GotItTooltip tooltip = new MyGotItToolTip(
                            "",
                            "",
                            "",
                            () -> {
                                ActionManager actionManager = ActionManager.getInstance();
                                String actionId = "quickAskShortCutAction";
                                AnAction action = actionManager.getAction(actionId);
                                System.out.println(action);
                                DataContext dataContext = DataManager.getInstance().getDataContext();
                                ActionUtil.performActionDumbAware(action, new AnActionEvent(null, dataContext, ActionPlaces.UNKNOWN, new Presentation(), ActionManager.getInstance(), 0));
                            });
                    tooltip.show( editor );

                });


            }
        };

        // 延迟1秒后执行任务
        timer.schedule(task, 1000);

    }



}
