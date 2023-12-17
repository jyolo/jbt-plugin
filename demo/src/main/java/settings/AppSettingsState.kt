package settings

import com.common.Env
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

//import com.obiscr.tabnine.inline.render.GraphicsUtils
//
//val settingsDefaultColor = GraphicsUtils.niceContrastColor.rgb

/**
 * This package (`userSettings`) is heavily influenced by the docs from here:
 * https://plugins.jetbrains.com/docs/intellij/settings-tutorial.html
 *
 *
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(name = "setting.AppSettingsState", storages = [Storage("TabnineSettings.xml")])
class AppSettingsState : PersistentStateComponent<AppSettingsState?> {
    var useDefaultColor: Boolean = false
    var logFilePath: String = ""
    var logLevel: String = ""
    var debounceTime: Long = 0
    var autoImportEnabled: Boolean = true
    var binariesFolderOverride: String = ""
    var cloud2Url: String = "http://127.0.0.1:8003"
    var useIJProxySettings: Boolean = true

    var enabled = true
    var inReasoning = false
    var iconHighlight = false
    var fillMode = true
    var betaMode = false
    var tabAfterFlag = false

    var resultContent = ""
    var lastTriggerContent = ""
    var lastTriggerOffset = 0

    // for dev
    var qianLiuServerUrl: String = Env.qianLiuServerUrl
    // webview 中反向代理的 url，后缀 /server
    var webviewProxyUrlSuffix = "/server"
    var webviewProxyUrl: String = "http://127.0.0.1:{port}" + webviewProxyUrlSuffix

    var serverUrl: String = Constants.DEFAULT_SERVER_HOST
    var engine: String = Constants.DEFAULT_ENGINE
    var apiToken: String = ""
    var maxTokens: Int = Constants.DEFAULT_MAX_TOKENS
    var maxLines: Int = Constants.DEFAULT_MAX_LINES
    var delayTime: Int = Constants.DELAY_TIME
    var shortcutKeys: String = Constants.SHORTCUTKEYS

//    private var colorState = settingsDefaultColor

//    var disableLanguageTableElement: List<DisableLanguageTableElement> = ArrayList()


    override fun getState(): AppSettingsState {
        return this
    }

    override fun loadState(state: AppSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        @JvmStatic
        val instance: AppSettingsState
            get() = ApplicationManager.getApplication().getService(AppSettingsState::class.java)
    }
}
