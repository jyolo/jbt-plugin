import com.intellij.execution.Platform;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.util.messages.MessageBus;
import com.obiscr.tabnine.userSettings.AppSettingsState;
import com.qianliuAiUi.ideActions.editor.CloseDocumentListener;
import com.qianliuAiUi.ideActions.editor.SchemeChangeListener;
import com.qianliuAiUi.ideActions.editor.SelectDocumentListener;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class StartUpActivity implements StartupActivity.DumbAware{

    private Server server;
    private JcefBrowserService jcefBrowserService;

    @Override
    public void runActivity(@NotNull Project project) {

        jcefBrowserService = JcefBrowserService.getInstance(project);
        startWebServer();
        // 监听当前正在编辑的文档
        ApplicationManager.getApplication().invokeLater(() -> {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor != null){
//                Document document = editor.getDocument();
//                document.addDocumentListener(new ChangeDocumentListener());
                FileEditorManager.getInstance(project).addFileEditorManagerListener(new CloseDocumentListener());
            }
        });

        // 监听 主题修改的事件
        ApplicationManager.getApplication().invokeLater(() -> {
            MessageBus messageBus = ApplicationManager.getApplication().getMessageBus();
            messageBus.connect().subscribe(EditorColorsManager.TOPIC, new SchemeChangeListener());
        });

        // 监听 选中的文本
        ApplicationManager.getApplication().invokeLater(() -> {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if(editor !=null ){
                editor.getSelectionModel().addSelectionListener(new SelectDocumentListener(project));
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
            // 随机系统可用端口
            server = new Server(0);
            AppSettingsState settings = AppSettingsState.getInstance();
            String qianLiuServerUrl = settings.getQianLiuServerUrl();
            String webviewProxyUrlSuffix = settings.getWebviewProxyUrlSuffix();
            // 添加反向代理
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS | ServletContextHandler.NO_SESSIONS);
            context.setContextPath("/");
            ServletHolder proxyServlet = context.addServlet(CustomProxyServlet.class, webviewProxyUrlSuffix + "/*");
            proxyServlet.setInitParameter("proxyTo", qianLiuServerUrl);
            proxyServlet.setInitParameter("prefix", webviewProxyUrlSuffix);

            // 设置静态资源目录
            ResourceHandler resourceHandler = new ResourceHandler();
            Resource resource = Resource.newClassPathResource("/html");
            if(resource == null){
                resource = Resource.newResource(getClass().getClassLoader().getResource("html/"));
            }
            resourceHandler.setBaseResource(resource);

            // 将静态资源处理器添加到 ServletHandler 中
            HandlerList handlers = new HandlerList();
            handlers.setHandlers(new Handler[] { resourceHandler, context });
            server.setHandler(handlers);
            server.start();
            ServerConnector connector = (ServerConnector) server.getConnectors()[0];
            jcefBrowserService.serverPort = connector.getLocalPort();
            System.out.println("----------server started--------------当前端口：" + jcefBrowserService.serverPort);


        } catch (Exception e) {
            System.out.println("----------server start error：" + e.getMessage());
            e.printStackTrace();
        }
    }


}