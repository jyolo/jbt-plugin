package settings;

import com.common.Env;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class Constants {
    public static final String PLUGIN_NAME = "QianLiuCopilot";
    public static final String PLUGIN_ID = "11111";
    public static final int DEFAULT_MAX_TOKENS = 30;
    public static final int DEFAULT_MAX_LINES = 120;
    public static final int DELAY_TIME = 500;
    //    public static final int MIN_DELAY_TIME = 500;
    public static final int MAX_DELAY_TIME = 3000;
    public static final boolean DEFAULT_ENABLED = true;
    public static final String DEFAULT_ENGINE = "codegen";
    public static final String DEFAULT_MODEL = "fastertransformer";
    public static final String DEFAULT_BASE_HOST = Env.qianLiuServerUrl;
    public static final String DEFAULT_SERVER_HOST = Env.qianLiuServerUrl + "/codepilot/v1";
    public static final String CODE_COMPLETION_UPLOAD_URL = Env.qianLiuServerUrl + "/api/users/code_completion_log";
    public static final String SNIPPET_UPLOAD_URL = Env.qianLiuServerUrl + "/v1/snippet";
    public static final String DEFAULT_SERVER_URI = "completions";
    public static final String SHORTCUTKEYS = "alt pressed A";
    public static final String SHORTCUTKEYS_TRIGGER = "manual";
    public static final String AUTO_TRIGGER = "auto";
    public static final Icon COPILOT_ICON = IconLoader.getIcon("/images/wx_pay.png", AllIcons.Icons.class);
    //    public static final String DEFAULT_SERVER_URL = "http://10.72.6.250:5000/v1/engines/codegen/completions";
    public static final String[]  CODE_COMPLETION_ALLOWABLE_LANGUAGES = {"vue", "typescript", "javascript", "python",
            "go", "c", "c++", "shell", "bash", "batch", "lua", "java", "php", "ruby"};
}
