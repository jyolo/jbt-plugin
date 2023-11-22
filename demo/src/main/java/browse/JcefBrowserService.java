package browse;

import com.intellij.execution.Platform;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;
import com.intellij.ui.jcef.JBCefJSQuery;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.*;
import org.cef.handler.*;
import org.cef.misc.BoolRef;
import org.cef.misc.StringRef;
import org.cef.network.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import static org.cef.CefSettings.LogSeverity.LOGSEVERITY_ERROR;

public class JcefBrowserService {
    public String serverHost = "http://127.0.0.1";
    public Integer serverPort = 0;
    private CefBrowser browser;
    private CefFrame cefframe ;
    private Component browseComponet ;
    private JPanel browsePanel ;
    private Integer ideVersion;
    public JBCefJSQuery myJSQuery;
    public Boolean webBrowseLoadEnd = false;
    private Boolean reloadToolWindow = false;
    public JBCefBrowser jbcefBrowser;
    public ToolWindow toolWindow;

    public Project project;
    public Boolean alreadyGetConfig = false;

    public void setAlreadyGetConfig(Boolean flag){
        //webview 成功获取到 IDE 的配置， 标记为true ,表示浏览器已经初始化完成
        this.alreadyGetConfig = flag;
    }
    public Boolean getAlreadyGetConfig(){
        //webview 成功获取到 IDE 的配置， 标记为true ,表示浏览器已经初始化完成
        return this.alreadyGetConfig;
    }

    public void setToolwindow(ToolWindow toolwindow) {
        this.toolWindow = toolwindow;
    }
    public void setProject(Project project){
        this.project = project;
    }
    public int getServerPort(){
        return serverPort;
    }
    public Boolean getWebBrowseLoadEnd(){
        return this.webBrowseLoadEnd;
    }
    public static JcefBrowserService getInstance(@NotNull Project project) {
        // 获取实例的时候 设置 jecf 系统级别配置项
        System.setProperty("ide.browser.jcef.log.level", "debug");
        System.setProperty("ide.browser.jcef.gpu.disable", "true");
        Registry.get("ide.browser.jcef.gpu.disable").setValue(true);
        Registry.get("ide.browser.jcef.gpu.disable.restartRequired").setValue(false);
        // 获取当前日期并格式化为"yyyy_MM_dd"格式
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        String formattedDate = currentDate.format(formatter);
        // 将日期添加到日志文件名中
        String logFileName = "jcef_" + formattedDate + ".log";
        String logFilePath = PathManager.getLogPath()  + Platform.current().fileSeparator + logFileName;
        // 检查日志目录是否存在，如果不存在则创建它
        File logDirectory = new File(PathManager.getLogPath());
        if (!logDirectory.exists()) { logDirectory.mkdirs();}
        // 设置日志文件路径
        System.setProperty("ide.browser.jcef.log.path", logFilePath);

        return project.getService(JcefBrowserService.class);
    }
    public CefBrowser getBrowser() {
        JBCefBrowser jbCefBrowser;
        if (browser == null) {
            boolean offScreenRendering = true;
            if(SystemInfo.isLinux){
                offScreenRendering = false;
            }
            String version = ApplicationInfo.getInstance().getFullVersion();
            System.out.println("version-----------------");
            String[] parts = version.split("\\.");
            ideVersion = Integer.parseInt(parts[0]);

            if (ideVersion >= 2022 ){ //2022-2023
                System.out.println("start ------------- createJBCefBrowser windows version: " + ideVersion);
                try {
                    System.out.println("createJBCefBrowser done---0--------------offScreenRendering:" + offScreenRendering );
                    jbCefBrowser = JBCefBrowser.createBuilder()
                            .setOffScreenRendering(offScreenRendering)
                            .setEnableOpenDevToolsMenuItem(true)
                            .build();

                    System.out.println("createJBCefBrowser done---1--------------" );
                    jbCefBrowser.setProperty(JBCefBrowser.Properties.FOCUS_ON_SHOW, Boolean.TRUE);
                    System.out.println("createJBCefBrowser done---2--------------" );

                }catch (Exception e){
                    System.out.println("createJBCefBrowser error-----------------" + e.getMessage());
                    e.printStackTrace();
                    return null;
                }

            }
            else{ // 2020-2021
                System.out.println("createJBCefBrowser version-----------------" + ideVersion);
                jbCefBrowser = new JBCefBrowser(getServerUrl());
            }

            System.out.println("createJBCefBrowser done---3--------------" );
            // 创建 client
            browser = jbCefBrowser.getCefBrowser();
            jbcefBrowser = jbCefBrowser;
            JBCefClient client = jbCefBrowser.getJBCefClient();
            // 这里runIde的时候可能会报warn，但不要修改以确保低版本的兼容性
            myJSQuery = JBCefJSQuery.create(jbcefBrowser);
            System.out.println("createJBCefBrowser done---33--------------" + myJSQuery);
            myJSQuery.addHandler((args) -> {
                System.out.println("JS 调用了 postMessageToIde，参数是：" + args);
                if (args != null){
                    System.out.println("JS 调用了 postMessageToIde，参数类型是：" + args.getClass());
                }
                if ("null".equals(args)) {
                    return new JBCefJSQuery.Response(null, 1, "不允许 null");
                } else if ("undefined".equals(args)) {
                    return new JBCefJSQuery.Response(null); // 这样 JS 侧，会掉 onFailure
                }
                // 返回值 统一为 json 字符串格式
                return new JBCefJSQuery.Response(new JsBridgeService(project).callAction(args));
            });

            // 创建 Handler
            client.addLoadHandler(loadHandlerAdapter(), browser);
            client.addContextMenuHandler(getCefContextMenuHandler(), browser);
            client.addLifeSpanHandler(lifeSpanHandler(), browser);
//            client.addRequestHandler(requestHandler(), browser);
//            client.addDisplayHandler(displayHandler(), browser);
            System.out.println("createJBCefBrowser done---4--------------" );
        }

        return browser;
    }

    public CefFrame getCefframe() {
        return cefframe;
    }

    public JPanel getBrowsePanel(CefBrowser browse){
        if(browsePanel == null){
            browsePanel = new JPanel(new GridBagLayout());
            browsePanel.setVisible(true);

            browse.setFocus(true);
            browse.setWindowVisibility(true);
            // 将 JBCefBrowser 的UI控件设置到Panel中
            browseComponet = jbcefBrowser.getComponent();

            // 设置最小尺寸
            int minWidth = 600; // 最小宽度
            int minHeight = 600; // 最小高度
            browseComponet.setVisible(true);
            browseComponet.setMinimumSize(new Dimension(minWidth, minHeight));
            System.out.println("createWebBrowseComponet done---0--------------" +  browseComponet);

            // 设置 GridBagConstraints，使组件水平和垂直都能自适应
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            // 将 JBCefBrowser 的 UI 控件设置到面板中
            browsePanel.add(browseComponet, gbc);
            browsePanel.setVisible(true);
            browsePanel.setMinimumSize(new Dimension(minWidth, minHeight));
        }
        return browsePanel;
    }
    public String getServerUrl(){
        int curTime = (int) (System.currentTimeMillis());
        return serverHost +":"+ serverPort +"/qianliuAiUi.html?rand=" + curTime;
    }

    public void checkToolWindowShowup() {
        Timer timer = new Timer(1000, new ActionListener() {
            private int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(browser != null){

                    if (reloadToolWindow == true) {
                        System.out.println("checkToolWindowShowup stop timer reloadToolWindow: " + reloadToolWindow);
                        ((Timer) e.getSource()).stop(); // 停止定时器
                        return;
                    }

                    if(ideVersion < 2022){
                        webBrowseLoadEnd = true;
                        ((Timer) e.getSource()).stop(); // 停止定时器
                        return;
                    }
                    System.out.println("wait for start reloadToolWindow------" + count);
                    Component CefBrowserUIComponent = jbcefBrowser.getCefBrowser().getUIComponent();
                    // 5 秒还没有 加载完成则 重载
                    if(ideVersion == 2022 && CefBrowserUIComponent.isShowing() &&
                            webBrowseLoadEnd == false  &&
                            reloadToolWindow == false &&
                            count >= 5
                    ){
                        System.out.println("start reloadToolWindow------at 1 count:" + count);
                        rebuildToolWindow(count);
                        ((Timer) e.getSource()).stop(); // 停止定时器
                        return;
                    }

                    //2022-2023 组件显示，已加载完成， 再进行重建组件
                    if(CefBrowserUIComponent.isShowing() &&
                            webBrowseLoadEnd == true &&
                            reloadToolWindow == false
                    ){
                        System.out.println("start reloadToolWindow------at 2 count:" + count);
                        rebuildToolWindow(count);
                        ((Timer) e.getSource()).stop(); // 停止定时器
                        return;
                    }

                }
                count++;
            }
        });
        timer.start();
    }

    public void showBrowser(){
        // 创建 JBCefBrowser 对象，并在后台线程中等待它加载server 启动完成
        CompletableFuture<CefBrowser> future = CompletableFuture.supplyAsync(() -> {
            for(int i = 0; i < 20; i++) { // 最多循环10次，每次间隔1秒
                System.out.println("start check server status at time " + i + " with port: " + serverPort);
                try {
                    if(serverPort == 0){
                        System.out.println("Port is 0, wait for server start----");
                        Thread.sleep(1000);
                        continue;
                    }
                    try (Socket ignored = new Socket("localhost", serverPort)) {
                        // Port is not available, already in use
                        System.out.println("Port is not available, already in use start createJBCefBrowser");
                        CefBrowser browser = getBrowser();
                        System.out.println("createJBCefBrowser --------- finnish " + browser);
                        return browser;
                    } catch (IOException e) {
                        System.out.println("check server status error --------- e" + e.getMessage() );
                    }
                    Thread.sleep(1000);
                } catch (Exception e1) {
                    System.out.println("check server status error e1 --------- e1" + e1.getMessage() );
                    e1.printStackTrace();
                }
            }
            return null;
        });

        future.thenAccept(CefBrowser -> {
            if (CefBrowser == null){
                System.out.println("error ---------- createWebBrowseComponet: jbcefBrowse is null");
                return;
            }

            // 后台创建 WebBrowseComponet 不阻塞主线程
            SwingUtilities.invokeLater(() -> {
                System.out.println("start ------------- createWebBrowseComponet");
                JPanel browsePanel = getBrowsePanel(CefBrowser);
                afterBrowseCreate(browsePanel);
            });

        });
    }

    public void afterBrowseCreate(JPanel browsePanel){
        System.out.println("createWebBrowseComponet done---1--------------browsePanel " + browsePanel);
        ContentFactory contentFactory = toolWindow.getContentManager().getFactory();
        Content content = contentFactory.createContent(browsePanel, "工具栏", true);
        content.setCloseable(false); // 禁用tab选项的关闭功能

        if (!toolWindow.isActive()) {
            toolWindow.activate(null);
            toolWindow.show();
        }
        System.out.println("createWebBrowseComponet done---2--------------content " + content);
        toolWindow.getContentManager().addContent(content, 0);
        // 2022-2023 组件创建完成后进行重载，避免首次加载渲染不出来的问题
//                checkToolWindowShowup();
    }

    public void rebuildToolWindow(long count){
        SwingUtilities.invokeLater(() -> {
            reloadToolWindow = true;
            disposeBrowser(true);
            System.out.println("end reloadToolWindow------" + count);
        });
    }

    public void disposeBrowser(Boolean rebuild) {
        if (browser != null) {
            SwingUtilities.invokeLater(() -> {
                System.out.println("start disposeBrowser------ start" );
                browser.stopLoad();
                browser.close(true);
                CefFrame frame = browser.getFocusedFrame();
                if(frame != null ){
                    System.out.println("start reloadToolWindow-----frame dispose");
                    frame.dispose();
                }

                System.out.println("start reloadToolWindow-----jbcefBrowser.dispose start" );
                jbcefBrowser.dispose();
                if(jbcefBrowser.isDisposed()){
                    System.out.println("start reloadToolWindow-----jbcefBrowser.dispose done" );
                }

                browser = null;
                browsePanel = null;
                jbcefBrowser = null;

                if(toolWindow != null){
                    toolWindow.setToHideOnEmptyContent(false);
                    Content content = toolWindow.getContentManager().getContent(0);
                    if(content != null){
                        System.out.println("start reloadToolWindow-----removeContent");
                        toolWindow.getContentManager().removeContent(content, true);
                    }
                }

                if(rebuild == true){
                    showBrowser();
                }

            });

        }
    }
    public CefContextMenuHandler getCefContextMenuHandler(){
        return new CefContextMenuHandler() {
            @Override
            public void onBeforeContextMenu(CefBrowser cefBrowser, CefFrame cefFrame, CefContextMenuParams cefContextMenuParams, CefMenuModel cefMenuModel) {
                // 清空右键菜单
                cefMenuModel.clear();
                // 有环境变量dev=1的时候才添加右键菜单
                String dev = System.getenv("dev");
                if (dev != null && dev.equals("1")){
                    // 添加菜单项
                    cefMenuModel.addItem(CefMenuModel.MenuId.MENU_ID_RELOAD, "刷新");
                    cefMenuModel.addItem(CefMenuModel.MenuId.MENU_ID_USER_LAST, "开发者工具");
                }

            }
            @Override
            public boolean onContextMenuCommand(CefBrowser cefBrowser, CefFrame cefFrame, CefContextMenuParams cefContextMenuParams, int commandId, int eventFlags) {
                if (commandId == CefMenuModel.MenuId.MENU_ID_RELOAD) {
                    cefBrowser.reload();
                    return true;
                }
                return false;
            }
            @Override
            public void onContextMenuDismissed(CefBrowser cefBrowser, CefFrame cefFrame) {}
        };
    }
    public CefLoadHandlerAdapter loadHandlerAdapter(){
        return new CefLoadHandlerAdapter() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                System.out.println("onLoadingStateChange ------------" + isLoading);
                if(!isLoading){
                    System.out.println("onLoadingStateChange ----------------加载完成" + isLoading);
                }
            }
            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                System.out.println("------>load started");
            }
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                cefframe = frame;
                // 标记已经加载页面完毕
                if(myJSQuery != null ){
                    browser.executeJavaScript(
                            "window."+ JsBridgeService.getJsPostFuncName() + " = function(arg) {" +
                                    myJSQuery.inject(
                                            "JSON.stringify(arg)",
                                            "response => window.postMessage(JSON.parse(response)) ",
                                            "(error_code, error_message) => console.log('callJava 失败' + error_code + error_message)"
                                    ) +
                                    "};",
                            frame.getURL(), 0);
                }

                System.out.println("------>load end jbcefBrowser" + jbcefBrowser);
                browser.setFocus(true);
                browser.setWindowVisibility(true);
//                if(toolWindow != null){
//                    SwingUtilities.invokeLater(() -> {
//                        java.util.List<AnAction> actionList = new ArrayList<>();
////                        actionList.add(new RefreshWebViewAction(project));
//                        // 生产环境不要打开
//                        actionList.add(new DevToolWebViewAction(project));
//                        toolWindow.setTitleActions(actionList);
//                    });
//                }

                webBrowseLoadEnd = true;

            }
            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                browser.setFocus(true);
                browser.setWindowVisibility(true);

                System.out.println("------>load error");
                System.out.println(errorCode);
                System.out.println(errorText);
                System.out.println(failedUrl);
                System.out.println("------>load error reloadURL " + failedUrl);
            }
        };
    }

    public CefDisplayHandler displayHandler (){
        return new CefDisplayHandler() {
            @Override
            public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {}

            @Override
            public void onTitleChange(CefBrowser browser, String title) {}

            @Override
            public boolean onTooltip(CefBrowser browser, String text) {
                return false;
            }

            @Override
            public void onStatusMessage(CefBrowser browser, String value) {
                System.out.println("onStatusMessage------value: " + value);
            }

            @Override
            public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level, String message, String source, int line) {
                if(level == LOGSEVERITY_ERROR){
                    System.out.println("onConsoleMessage------Console message: " + message);
                    System.out.println("onConsoleMessage------level: " + level);
                    System.out.println("onConsoleMessage------source: " + source);
                    System.out.println("onConsoleMessage------line: " + line);
                }
                return false;
            }

            @Override
            public boolean onCursorChange(CefBrowser browser, int cursorType) {
                return false;
            }
        };
    }

    public CefRequestHandler requestHandler(){

    }
    public CefLifeSpanHandler lifeSpanHandler(){
        return new CefLifeSpanHandler() {
            @Override
            public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
                return false;
            }

            @Override
            public void onAfterCreated(CefBrowser browser) {
                if (ideVersion >= 2022 ){
                    jbcefBrowser.loadURL(getServerUrl());
                }

                SwingUtilities.invokeLater(() -> {
                    System.out.println("CefLifeSpanHandler-------onAfterCreated" + browser.getUIComponent().getSize());
                    // 设置最小尺寸
                    int minWidth = 600; // 最小宽度
                    int minHeight = 600; // 最小高度
                    browser.getUIComponent().setMinimumSize(new Dimension(minWidth, minHeight));
                    browser.getUIComponent().setVisible(true);
                    browser.getUIComponent().repaint();
                    browser.getUIComponent().requestFocus();
                    browser.getUIComponent().requestFocusInWindow();
                    browsePanel.repaint();
                    browsePanel.setVisible(true);
                    browsePanel.requestFocus();
                    browsePanel.requestFocusInWindow();
                    System.out.println(browser.getUIComponent().getSize());
                    System.out.println(browsePanel.getSize());
                });
            }

            @Override
            public void onAfterParentChanged(CefBrowser browser) {
                System.out.println("CefLifeSpanHandler-------onAfterParentChanged");
            }

            @Override
            public boolean doClose(CefBrowser browser) {
                System.out.println("CefLifeSpanHandler-------doClose");
                return false;
            }

            @Override
            public void onBeforeClose(CefBrowser browser) {
                if(jbcefBrowser != null){
                    CefCookieManager cefCookieManager = jbcefBrowser.getJBCefCookieManager().getCefCookieManager();
                    System.out.println(cefCookieManager.visitAllCookies(new CefCookieVisitor() {
                        @Override
                        public boolean visit(CefCookie cookie, int count, int total, BoolRef delete) {
                            System.out.println("CefLifeSpanHandler-------onBeforeClose---clear cookie start");
                            Boolean cleared = cefCookieManager.deleteCookies(serverHost +":"+ serverPort , cookie.name);
                            System.out.println("CefLifeSpanHandler----------clear cookie cookie.name : " + cookie.name +"："+ cleared);
                            System.out.println("CefLifeSpanHandler-------onBeforeClose---clear cookie end");
                            return false;
                        }
                    }));
                }

            }
        };
    }

}



