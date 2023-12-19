package com.test;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefRequest;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;


class AiReviewWindowFactory implements ToolWindowFactory {
    private Server server;
    public Integer serverPort = 12345;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        // 创建一个垂直布局的面板
//        JPanel webpanel = new JPanel(new BorderLayout());
        JPanel webpanel = new JPanel(new GridBagLayout());

        if (!JBCefApp.isSupported()) {
            String message = "The current IDE does not support Online ChatGPT, because the JVM RunTime does not support JCEF.\n" +
                    "\n" +
                    "Please refer to the following settings: \nhttps://chatgpt.en.obiscr.com/faq/#4-plugins-are-not-available-in-android-studio";
            System.out.println(message);
        }

        startWebServer();

        int curTime = (int) (System.currentTimeMillis());
        String url = "http://127.0.0.1:"+ serverPort +"/index.html?rand=" + curTime;

        JBCefBrowser jbCefBrowser;
        jbCefBrowser = new JBCefBrowser(url);

        // 创建 client
        JBCefClient client = jbCefBrowser.getJBCefClient();
        client.addLoadHandler(new CefLoadHandlerAdapter() {
            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {
                System.out.println("------>load started");
            }
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                System.out.println(httpStatusCode);
                System.out.println("------>load end");
                // 在 onLoadEnd 方法中调用 JavaScript 函数
                browser.executeJavaScript("addDivToBody('java');", frame.getURL(), 0);
            }
            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                System.out.println("------>load error");
                System.out.println(errorCode);
                System.out.println(errorText);
            }
        }, jbCefBrowser.getCefBrowser());

//        jbCefBrowser.setErrorPage((errorCode, errorText, failedUrl) -> (errorCode == CefLoadHandler.ErrorCode.ERR_ABORTED) ?
//                null : JBCefBrowserBase.ErrorPage.DEFAULT.create(errorCode, errorText, failedUrl));
//        jbCefBrowser.setProperty(JBCefBrowser.Properties.FOCUS_ON_SHOW, Boolean.TRUE);

        // 将 JBCefBrowser 的UI控件设置到Panel中
        Component comppent = jbCefBrowser.getComponent();
        comppent.setVisible(true);

        // 设置 GridBagConstraints，使组件水平和垂直都能自适应
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // 将 JBCefBrowser 的 UI 控件设置到面板中
        webpanel.add(jbCefBrowser.getComponent(), gbc);

        ContentFactory contentFactory = toolWindow.getContentManager().getFactory();

        Content content = contentFactory.createContent(webpanel, "ttt", false);
        // 将 Content 对象添加到 ToolWindow 的内容管理器中
        toolWindow.getContentManager().addContent(content,0);

        toolWindow.addContentManagerListener(new ContentManagerListener() {
            @Override
            public void selectionChanged(@NotNull ContentManagerEvent event) {
                String displayName = event.getContent().getDisplayName();
                System.out.println(displayName);
            }
        });

    }

    public boolean isPortAvailable(int port) {
        try (Socket ignored = new Socket("localhost", port)) {
            // Port is not available, already in use
            return false;
        } catch (IOException e) {
            // Port is available, not in use
            return true;
        }
    }

    public void startWebServer() {
        try {

            boolean isavailable = isPortAvailable(serverPort);
            if(!isavailable){
                System.out.println("端口不可用");
                return ;
            }
            server = new Server(serverPort);
            ResourceHandler resourceHandler = new ResourceHandler();

            Resource resource = Resource.newClassPathResource("/html");
            if(resource == null){
                 resource = Resource.newResource(getClass().getClassLoader().getResource("html/"));
            }
            resourceHandler.setBaseResource(resource);
            server.setHandler(resourceHandler);
            server.start();
            System.out.println("server started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopWebServer() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
                System.out.println("server started");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
