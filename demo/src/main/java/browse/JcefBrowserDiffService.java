package browse;

import com.intellij.execution.Platform;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.ui.jcef.JBCefJSQuery;
import quickAsk.MyDialogDiff;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class JcefBrowserDiffService extends JcefBrowserService{

    public Map<String, Object> webviewParams;
    public MyDialogDiff myDialogDiff;

    public static JcefBrowserDiffService getInstance(@NotNull Project project) {
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

        return project.getService(JcefBrowserDiffService.class);
    }

    public void setServerPort(int port){
        this.serverPort = port;
    }
    public Map<String, Object> getWebviewParams(){
        return webviewParams;
    }
    public String getServerUrl(){
        int curTime = (int) (System.currentTimeMillis());
        return serverHost +":"+ serverPort +"/diff.html?rand=" + curTime;
    }

    public void showBrowser(Map<String, Object> webviewParams){
        this.webviewParams = webviewParams;
        super.showBrowser();
    }
    public void afterBrowseCreate(JPanel browsePanel)  {
        myDialogDiff = new MyDialogDiff();
        ApplicationManager.getApplication().invokeLaterOnWriteThread(()-> {
            myDialogDiff.setProject(project);
            myDialogDiff.setBrowsePanel(browsePanel);
            myDialogDiff.showUp();
        });

//        // 检查浏览器完成后 弹出 openDevtools
//        Timer checker = new Timer(100, new ActionListener() {
//            private int count = 0;
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                System.out.println("webBrowseLoadEnd---" + webBrowseLoadEnd);
//                if(webBrowseLoadEnd){
//                    jbcefBrowser.openDevtools();
//                    ((Timer) e.getSource()).stop(); // 停止定时器
//                }
//            }
//        });
//        checker.start();

    }

    public void closeDialogDiff(){
        myDialogDiff.doCancelAction();
    }
}
