package quickAsk;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.IdeFrame;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.components.JBPanel;
import browse.JcefBrowserDiffService;
import javax.swing.*;
import java.awt.*;


public class MyDialogDiff extends DialogWrapper {
    private JBPanel centerPanel;
    private Project project;
    private JPanel browsePanel;
    public int diffWindowWidth ;
    public int diffWindowHeight ;

    public MyDialogDiff() {
        super(true);
        setTitle("");
        Dimension windowSize = getWindowSize();
        diffWindowWidth = windowSize.width - 100;
        diffWindowHeight = windowSize.height - 100;
        // 设置对话框的大小
        setSize(diffWindowWidth, diffWindowHeight);
        setUndecorated(false);
    }

    private Dimension getWindowSize() {
        String version = ApplicationInfo.getInstance().getFullVersion();
        System.out.println("version-----------------");
        String[] parts = version.split("\\.");
        int ideVersion = Integer.parseInt(parts[0]);

        WindowManager windowManager = WindowManager.getInstance();
        if (windowManager != null) {
            IdeFrame ideFrame = windowManager.getIdeFrame(project);
            if (ideFrame != null) {
                return ideFrame.getComponent().getSize();
            }
        }
        return new Dimension(800, 600); // 默认大小
    }

    public void setProject(Project project){
        this.project = project;
    }
    public void setBrowsePanel(JPanel browsePanel){
        this.browsePanel = browsePanel;
    }
    public void showUp(){
        init();
        super.show();
    }

    @Override
    public void doCancelAction() {
        JcefBrowserDiffService jcefBrowserDiffService = JcefBrowserDiffService.getInstance(project);
        jcefBrowserDiffService.disposeBrowser(false);
        super.doCancelAction();
    }
    @Override
    protected JComponent createCenterPanel() {
        centerPanel = new JBPanel(new BorderLayout());
        centerPanel.add( browsePanel, BorderLayout.CENTER);
        centerPanel.setPreferredSize(new Dimension(diffWindowWidth, diffWindowHeight));
        return centerPanel;
    }
    protected JComponent createSouthPanel() {
        return null;
    }


}