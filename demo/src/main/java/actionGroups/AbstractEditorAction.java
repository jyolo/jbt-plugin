package actionGroups;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import settings.OpenAISettingsState;
import services.EditorGutterIconService;
import ideActions.StartUpActivity;
import org.jetbrains.annotations.NotNull;
import browse.JsBridgeService;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;

/**
 * @author Wuzi
 */
public abstract class AbstractEditorAction extends AnAction {

    protected String text = "";
    protected String key = "";

    // addTests、findProblems、optimize、explain、chat(default)
    public static final String ACTION_ADD_TESTS = "addTests";
    public static final String ACTION_FIND_PROBLEMS = "findProblems";
    //    public static final String ACTION_AI_REVIEW = "aiReview";
    public static final String ACTION_AI_REVIEW = "codeReview";
    public static final String ACTION_OPTIMIZE = "optimize";
    public static final String ACTION_EXPLAIN = "explain";

    public static final String ACTION_CONVERT = "languageConvert";
    public static final String ACTION_AI_CHAT = "aiChat";
    public static final String GENERATE_ADD_DEBUG_CODE = "addDebugCode";
    public static final String GENERATE_ADD_STRONGER_CODE = "addStrongerCode";
    public static final String GENERATE_ADD_COMMENT = "addComment";
    public static final String GENERATE_PICK_COMMON_FUNC = "pickCommonFunc";
    public static final String GENERATE_SIMPLIFY_CODE = "simplifyCode";


    public static Map<String, String> extensionMapLanguage = new HashMap<>(){{
        put("py", "python");
        put("cpp", "CPP");
        put("js", "javascript");
        put("java", "java");
        put("c", "c");
        put("cc", "c++");
        put("h", "c++");
        put("hh", "c++");
    }};

    public AbstractEditorAction(@NotNull Supplier<@NlsActions.ActionText String> dynamicText) {
        super(dynamicText);
    }

    public void customActionPerformed(@NotNull AnActionEvent e, String action, String customInstructions){
        doActionPerformed(e, proccessLanguage(e), action, customInstructions);
    }

    private String proccessLanguage(AnActionEvent e) {
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        String[] split = psiFile.getName().split("\\.");
        if (split.length > 0) {
            String extension = split[split.length - 1];
            if (extensionMapLanguage.containsKey(extension)) {
                return extensionMapLanguage.get(extension);
            }
        }
        return e.getData(CommonDataKeys.PSI_FILE).getLanguage().getDisplayName();
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("abandoned");
//        doActionPerformed(e);
    }

    public void doActionPerformed(@NotNull AnActionEvent e, String language, String action, String customInstructions) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;

        String selectedText = editor.getSelectionModel().getSelectedText();
        OpenAISettingsState setting = OpenAISettingsState.getInstance();

        // 组装所需 js 端 所需的数据
        String actionName = "menu.rightKeyMenu";
        Map<String, Object> params = new HashMap<>();
        Map<String, String> customInstructionMap = new HashMap<>();

        // 右键菜单事件的类型
        params.put("menuType", action);
        // jetbrains 这边的回调action名字, js那边 执行完成后 判断是否有该参数就 有的话 就执行jetbrains的回调
        params.put("cbActionName", "menu.rightKeyMenuCallBack");
        // 用户选中的代码
        params.put("code", selectedText);

        // 选中代码的开始和结束的偏移量，用于 webview 修改选中的文本
        params.put("selectStart", editor.getSelectionModel().getSelectionStart());
        params.put("selectEnd", editor.getSelectionModel().getSelectionEnd());

        EditorGutterIconService editorGutterIconService = EditorGutterIconService.getInstance(e.getProject());
        if(editorGutterIconService.selectStart == -1){
            // 预设gutter 的图标显示的地方
            editorGutterIconService.setSelectStart(editor.getSelectionModel().getSelectionStart());
            editorGutterIconService.setSelectEnd(editor.getSelectionModel().getSelectionEnd());
        }

        params.put("customInstructions", customInstructions);
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        String filePath = file.getPath();
        params.put("language", language);
        params.put("filePath", filePath);

//        customInstructionMap.put(ACTION_OPTIMIZE, setting.optimizeInstructions);
        customInstructionMap.put(ACTION_ADD_TESTS, setting.addTestsInstructions);
        customInstructionMap.put(ACTION_EXPLAIN, setting.explainInstructions);
        customInstructionMap.put(ACTION_CONVERT, setting.languageConvertInstructions);
        customInstructionMap.put(ACTION_AI_REVIEW, setting.AICodeReviewInstructions);
        System.out.println("customInstructionMap__" + customInstructionMap);
        params.put("customInstructionMap", customInstructionMap);

        new JsBridgeService(e.getProject()).callActionFromIde(actionName, params);

    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        boolean hasSelection = editor.getSelectionModel().hasSelection();
        e.getPresentation().setEnabledAndVisible(hasSelection);
    }
}