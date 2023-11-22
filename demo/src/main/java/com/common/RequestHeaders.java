package com.common;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.extensions.PluginId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import settings.Constants;
import settings.OpenAISettingsState;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Wuzi
 */
public class RequestHeaders {
    private static final Logger LOG = LoggerFactory.getLogger(RequestHeaders.class);
    private final Map<String,String> headers = new HashMap<>();
    private OpenAISettingsState settings = OpenAISettingsState.getInstance();
    public static RequestHeaders getInstance() {
        return ApplicationManager.getApplication().getService(RequestHeaders.class);
    }
    private String ideName = ApplicationInfo.getInstance().getVersionName();
    private String ideVersion = PluginManager.getPlugin(PluginId.getId(Constants.PLUGIN_ID)).getVersion();
    private String ideRealVersion = ApplicationInfo.getInstance().getFullVersion() + "  " +
            ApplicationInfo.getInstance().getApiVersion();

    public Map<String, String> getChatGPTHeaders() {
        headers.put("Accept","text/event-stream");
        headers.put("Authorization","Bearer " + settings.accessToken);
        headers.put("Content-Type","application/json");
        headers.put("X-Openai-Assistant-App-Id","");
        headers.put("Connection","close");
        headers.put("Accept-Language","en-US,en;q=0.9");
        headers.put("Referer","https://chat.openai.com/chat");
        headers.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.1 Safari/605.1.15");
        return headers;
    }

    public Map<String, String> getGPT35TurboHeaders() {
//        headers.put("Authorization","Bearer " + settings.apiKey);
        headers.put("Content-Type","application/json");
        headers.put("User-Agent", ChatGPTBundle.message("system.setting.http.user_agent"));
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        headers.put("api-key", instance.apiKey);
        headers.put("plugin-version", ideVersion);
        headers.put("plugin-name", Constants.PLUGIN_NAME);
        headers.put("ide", ideName);
        headers.put("ide-version", ideVersion);
        headers.put("ide-real-version", ideRealVersion);
        return headers;
    }

}
