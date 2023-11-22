package com.qianliuAiUi.quickAsk;

import com.alibaba.fastjson2.JSONObject;
import com.common.util;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.UIUtil;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.tabnine.userSettings.AppSettingsState;
import com.qianliuAiUi.JcefBrowserDiffService;
import com.qianliuAiUi.JcefBrowserService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class MyDialogInput extends DialogWrapper {

    public JTextField input;
    private JPanel cancelPanelWarp;
    private JPanel centerPanel;
    private JPanel initPanelWarp;
    private JPanel responsePanelWarp;
    private JPanel SouthPanelWarp;
    private JLabel loadingLabel;
    public JPanel centerMainPanel;
    private JButton sendButton;
    private JButton cancelButton;
    private Timer loadingTimer;
    private JLabel bottonLabel;
    private Editor editor;
    private JTextArea responseTextArea;
    private AnActionEvent event;
    public MyDialogRequest request;
    public Project project;
    public String selectedText;
    public String language;
    public Integer startLine;
    public Integer endLint;
    public String theme;
    public JLabel sendBottonLabel;
    public Map<String, Object> webviewParams ;
    private int maxWidth = 600;
    private int centerPanelWidth = 35;
    public Boolean sendReqeustIsRunning = false; // 取消请求的标志，避免重复执行 cancelReqeustAction 重复渲染组件造成ide卡顿
    public MyDialogInput() {
        super(true);
        setTitle("");
        setUndecorated(false);
        init();
        pack();
    }

    public void setEditor(Editor editor){
        this.editor = editor;
    }

    public void setAnActionEvent(AnActionEvent event){this.event = event;}
    //    @Override
//    protected void createDefaultActions() {
//        super.createDefaultActions();
//    }
    @Override
    protected JComponent createCenterPanel() {
        // 创建一个新的JPanel，并将其设置为垂直布局管理器
        centerMainPanel = new JPanel();
        centerMainPanel.setLayout(new BoxLayout(centerMainPanel, BoxLayout.Y_AXIS));

        // 创建centerPanel
        centerPanel = new JPanel(new BorderLayout());
        input = new JTextField();
        input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // 文本插入事件
                System.out.println("输入改变，终止请求");
                cancelReqeustAction();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // 文本删除事件
                System.out.println("输入改变，终止请求");
                cancelReqeustAction();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // 文本改变事件
                System.out.println("输入改变，终止请求");
                cancelReqeustAction();
            }
        });

        // 暗色，亮色主题，都给输入框固定一个背景色
        Boolean isDarkTheme = UIUtil.isUnderDarcula();
        if (isDarkTheme){
            Color storedColor = new Color(69, 73, 74); // 浅灰色
            input.setBackground(storedColor);
        }else{
            Color storedColor = new Color(255, 255, 255); // 白色
            input.setBackground(storedColor);
        }

        input.setAutoscrolls(true);
        input.requestFocus(true);
        input.setBorder(BorderFactory.createEmptyBorder());

        KeyListener listener = historyListener();
        input.addKeyListener(listener);

        centerPanel.add(input, BorderLayout.CENTER);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // 设置左右边距 centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // 设置左右边距
        centerPanel.setPreferredSize(new Dimension(maxWidth, centerPanelWidth));

        // 创建responsePanelWarp
        responsePanelWarp = new JPanel(new BorderLayout());
        // 创建一个JTextArea对象，并将其添加到JScrollPane中
        responseTextArea = new JTextArea();
        responseTextArea.setEditable(false);
        responseTextArea.setBackground(null);
        responseTextArea.setLineWrap(true);
        responseTextArea.setWrapStyleWord(true);

        JScrollPane responsescrollPane = new JBScrollPane(responseTextArea); // 将JTextArea放置在JScrollPane中
        responsescrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // 设置垂直滚动条策略为需要时显示
        responsescrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // 设置水平滚动条策略为从不显示
        responsescrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        responsePanelWarp.add(responsescrollPane, BorderLayout.CENTER);
        responsePanelWarp.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // 设置左右边距 centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // 设置左右边距
        responsePanelWarp.setVisible(false);

        // 将centerPanel和responsePanelWarp添加到centerMainPanel中
        centerMainPanel.add(centerPanel);
        centerMainPanel.add(responsePanelWarp);
        return centerMainPanel;

    }

    @Override
    protected JComponent createSouthPanel() {
        initPanelWarp = getInitPanelWarp();
        initPanelWarp.setVisible(true);

        SouthPanelWarp = new JPanel(new BorderLayout());
        SouthPanelWarp.add(initPanelWarp);

        return SouthPanelWarp;
    }
    @Override
    protected void doOKAction() {
        if(project != null){
            return;
        }
        project = editor.getProject();
        request = new MyDialogRequest(project, this);
        String inputContent = input.getText();
        if(inputContent.isEmpty()){
            Messages.showWarningDialog("输入不能为空", "提示");
            input.requestFocus();
            input.requestFocusInWindow();
            project = null;
        }else{
            loadingTimer = new Timer(1000, new ActionListener() {
                private int count = 0;
                @Override
                public void actionPerformed(ActionEvent e) {
                    switch(count % 4) {
                        case 0:
                            loadingLabel.setText("正在输出结果");
                            break;
                        case 1:
                            loadingLabel.setText("正在输出结果.");
                            break;
                        case 2:
                            loadingLabel.setText("正在输出结果..");
                            break;
                        case 3:
                            loadingLabel.setText("正在输出结果...");
                            break;
                    }
                    System.out.println("loadingTimer run count: " + count);
                    count++;
                }

            });
            loadingTimer.start();

            // 绘制取消面板
            SwingUtilities.invokeLater(() -> {
                SouthPanelWarp.remove(initPanelWarp);
                cancelPanelWarp = getCancelPanelWarp();
                cancelPanelWarp.setVisible(true);
                SouthPanelWarp.add(cancelPanelWarp);
                SouthPanelWarp.revalidate();
                SouthPanelWarp.repaint();
            });

            // 发送请求
            SwingUtilities.invokeLater(() -> {
                selectedText = editor.getSelectionModel().getSelectedText();
                int selectStart = editor.getSelectionModel().getSelectionStart();
                int selectEnd = editor.getSelectionModel().getSelectionEnd();
                startLine = editor.getDocument().getLineNumber(selectStart) ; // 从0开始
                endLint = editor.getDocument().getLineNumber(selectEnd) ;
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                VirtualFile virtualFile = fileEditorManager.getSelectedFiles()[0];
                String filePath = virtualFile.getPath();
                language = util.getLanguageByFilePath(filePath);
                theme = UIManager.getLookAndFeel().getName().contains("Darcula") ? "vs-dark" : "vs";

                webviewParams = new HashMap<>(); // 重置为新的空的 map
                if(selectedText == null){
                    webviewParams.put("sourceCode", "");
                }else{
                    webviewParams.put("sourceCode", selectedText);
                }
                webviewParams.put("language", language);
                webviewParams.put("startLine", startLine);
                webviewParams.put("endLint", endLint);
                webviewParams.put("theme", theme);
                webviewParams.put("selectStart", selectStart);
                webviewParams.put("selectEnd", selectEnd);
                webviewParams.put("filePath", filePath);

                ExecutorService executorService = Executors.newSingleThreadExecutor();;
                executorService.submit(() -> {
                    // 处理请求
                    addHistory(inputContent);
                    OpenAISettingsState instance = OpenAISettingsState.getInstance();
                    request.doRequest(loadingTimer, inputContent, selectedText);
                });
                executorService.shutdown();
            });
        }
    }
    public void addHistory(String newData) {
        OpenAISettingsState instance = OpenAISettingsState.getInstance();
        LinkedHashSet<String> history = instance.historyInput;
        if (!newData.trim().equals("")  && history.contains(newData)) {
            return;
        }
        if (history.size() >= instance.historyLength) {
            String oldestData = history.iterator().next();
            history.remove(oldestData);
        }

        history.add(newData);
    }
    @Override
    public void doCancelAction() {
        if(loadingTimer != null){
            request.stopRequest();
            loadingTimer.stop();
        }
        super.doCancelAction();

    }
    public void showResponseTextByNoCode(String content, Boolean isErrorMsg){
        responseTextArea.setVisible(true);
        responseTextArea.setText(content);
        if(isErrorMsg){
            responseTextArea.setForeground(Color.red);
        }
        responseTextArea.validate();
        responseTextArea.repaint();

        Element paragraph = responseTextArea.getDocument().getDefaultRootElement();

        // 固定宽度下 一行内容
        if(paragraph.getEndOffset() < 40){
            responsePanelWarp.setPreferredSize(new Dimension(maxWidth, 30));
        }else if(paragraph.getEndOffset() < 60){
            responsePanelWarp.setPreferredSize(new Dimension(maxWidth, 45));
        }else if(paragraph.getEndOffset() >= 60){ // 二行内容
            responsePanelWarp.setPreferredSize(new Dimension(maxWidth, 60));
        } else if (paragraph.getEndOffset() >= 60 * 2) { // 超过三行
            responsePanelWarp.setPreferredSize(new Dimension(maxWidth, 70));
        }

        responsePanelWarp.setVisible(true);
        responsePanelWarp.validate();
        responsePanelWarp.repaint();

        loadingLabel.setText("");
        bottonLabel.setText("");
        loadingTimer.stop();
        pack();
    }
    public void afterRequestCallBack(JSONObject responseJson, Boolean isErrorMsg){
        SwingUtilities.invokeLater(() -> {
            Boolean noCode = false;
            // 处理响应
            String content = "";
            int error_code = responseJson.getIntValue("error_code", 0) ;
            String aiCode = "";
            String respId = "";
            Boolean success = responseJson.getBooleanValue("success", false) ;
            String message = responseJson.getString("message") ;
            OpenAISettingsState instance = OpenAISettingsState.getInstance();

            System.out.println("quick ask responseJson----------------");
            System.out.println(responseJson);
            System.out.println("quick ask responseJson----------------");

            JSONObject dataJson = responseJson.getJSONObject("data");
            if(dataJson != null){
                aiCode = dataJson.getString("code");
                respId = dataJson.getString("id");
            }

            if(instance.apiKey == ""){
                content = "请先配置Token，浏览器访问千流AI网页版（https://chat.sangfor.com），在页面左下角打开设置窗口获取Token，打开IDE设置页面 > 找到\"千流AI-你的AI编程助手\" > 配置Token后，重启IDE即可。";
                noCode = true;
            }else if(error_code > 0 ){
                if(error_code == 100002){
                    content = "token错误,请先检查token配置";
                }else{
                    content = responseJson.getString("message");
                }
                noCode = true;
            } else if(aiCode.equals("") && success == false){
                content = message;
                noCode = true;

            }

            if(noCode){
                showResponseTextByNoCode(content, isErrorMsg);
            }else{
                doCancelAction();
                webviewParams.put("AICode", aiCode);

                webviewParams.put("respId", respId);
                AppSettingsState settings = AppSettingsState.getInstance();
                String webviewProxyUrl = settings.getWebviewProxyUrl();
                // 将端口替换成当前的随机端口
                JcefBrowserService jcefBrowserService = JcefBrowserService.getInstance(project);
                webviewProxyUrl = webviewProxyUrl.replace("{port}", jcefBrowserService.serverPort.toString());

                webviewParams.put("token", instance.apiKey);
                webviewParams.put("baseUrl", webviewProxyUrl);

                JcefBrowserDiffService jcefBrowserDiffService = JcefBrowserDiffService.getInstance(project);
                jcefBrowserDiffService.setProject(project);
                jcefBrowserDiffService.setServerPort(JcefBrowserService.getInstance(project).getServerPort());
                jcefBrowserDiffService.showBrowser(webviewParams);
            }


        });
    }

    public JPanel getInitPanelWarp(){
        // 将sendButton和sendButtonlabel添加到一个JPanel中
        JPanel initButtonPanel = new JPanel(new BorderLayout());

        sendButton = createJButtonForAction(new sendReqeustAction(this::sendReqeustAction));
        sendButton.setDefaultCapable(true);
        sendButton.setText("发送");
        JRootPane rootPane = centerMainPanel.getRootPane();
        rootPane.setDefaultButton(sendButton);


        // 创建Cancel按钮
        sendBottonLabel = new JLabel("Esc快捷键退出");
        sendBottonLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // 设置左右边距


        initButtonPanel.add(sendButton, BorderLayout.WEST);
        initButtonPanel.add(sendBottonLabel, BorderLayout.CENTER);

        // 创建一个面板来放置Dialog的按钮
        JPanel sendPanel = new JPanel(new BorderLayout());
        sendPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0)); // 设置左右边距
        sendPanel.add(initButtonPanel, BorderLayout.WEST); // 最左边

        // 最外层
        JPanel Warp = new JPanel(new BorderLayout());
        Warp.add(sendPanel);

        return Warp;

    }

    public static final class cancelReqeustAction extends AbstractAction {
        private final @NotNull Runnable cancelReqeustActionPerformed;

        public cancelReqeustAction(@NotNull Runnable ActionPerformed) {
            cancelReqeustActionPerformed = ActionPerformed;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            cancelReqeustActionPerformed.run();
        }
    }
    public static final class sendReqeustAction extends AbstractAction {
        private final @NotNull Runnable sendReqeustAction;

        public sendReqeustAction(@NotNull Runnable ActionPerformed) {
            sendReqeustAction = ActionPerformed;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            sendReqeustAction.run();
        }
    }

    protected void cancelReqeustAction() {
        if(sendReqeustIsRunning == false){
            System.out.println("sendReqeustAction is not running");
            return;
        }else{
            //标记请求状态
            sendReqeustIsRunning = false;
        }

        if(loadingTimer != null){
            request.stopRequest();
            loadingTimer.stop();
        }
        // 绘制取消面板
        SwingUtilities.invokeLater(() -> {
            if(cancelPanelWarp != null){
                // 移除掉 取消面板 设置不可见
                SouthPanelWarp.remove(cancelPanelWarp);
                cancelPanelWarp.setVisible(false);
                // 响应文案的textarea 设置不可见
                responseTextArea.setVisible(false);
                responseTextArea.revalidate();
                responseTextArea.repaint();
                // 响应文案的面板 设置不可见
                responsePanelWarp.setVisible(false);
                responsePanelWarp.revalidate();
                responsePanelWarp.repaint();

                // 中间的面板重新绘制高度
                centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // 设置左右边距 centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // 设置左右边距
                centerPanel.setPreferredSize(new Dimension(maxWidth, centerPanelWidth));
                centerPanel.revalidate();
                centerPanel.repaint();

                // 按钮界面
                JRootPane rootPane = centerMainPanel.getRootPane();
                if(rootPane != null){
                    rootPane.setDefaultButton(sendButton);
                }
                initPanelWarp.setVisible(true);
                initPanelWarp.revalidate();
                initPanelWarp.repaint();
                SouthPanelWarp.add(initPanelWarp);
                SouthPanelWarp.revalidate();
                SouthPanelWarp.repaint();

            }

            pack();
        });

    }
    protected void sendReqeustAction(){
        sendReqeustIsRunning = true;
        project = null;
        doOKAction();
    }
    public JPanel getCancelPanelWarp(){
        cancelButton = createJButtonForAction(new cancelReqeustAction(this::cancelReqeustAction));
        cancelButton.setText("取消");

        // 创建Cancel按钮
        bottonLabel = new JLabel("Esc快捷键退出");
        // 将sendButton和sendButtonlabel添加到一个JPanel中
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(cancelButton, BorderLayout.WEST);
        bottonLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0)); // 设置左右边距
        buttonPanel.add(bottonLabel, BorderLayout.CENTER);

        // 创建一个面板来放置Dialog的按钮
        JPanel warpPanel = new JPanel(new BorderLayout());
        warpPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0)); // 设置左右边距
        loadingLabel = new JLabel("正在输出结果...");
        warpPanel.add(buttonPanel, BorderLayout.WEST); // 最左边
        warpPanel.add(loadingLabel, BorderLayout.EAST);   // 最右边

        // 最外层
        JPanel Warp = new JPanel(new BorderLayout());
        Warp.add(warpPanel);

        return Warp;

    }

    private KeyListener historyListener() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            private boolean ctrlPressed = false;


            @Override
            public void keyPressed(KeyEvent e) {
                OpenAISettingsState instance = OpenAISettingsState.getInstance();
                if (instance.historyInput.isEmpty()) {
                    return;
                }
                try {
                    String[] dataArray = instance.historyInput.toArray(new String[instance.historyLength]);
                    int keyCode = e.getKeyCode();
                    if (keyCode == KeyEvent.VK_CONTROL) {
                        ctrlPressed = true;
                        return;
                    } else if (ctrlPressed && keyCode == KeyEvent.VK_UP) {
                        instance.historyIndex = (instance.historyIndex - 1 + instance.historyInput.size()) %
                                instance.historyInput.size();
                    } else if (ctrlPressed && keyCode == KeyEvent.VK_DOWN) {
                        instance.historyIndex = (instance.historyIndex + 1) % instance.historyInput.size();
                    } else {
                        return;
                    }
                    int index = instance.historyIndex;
                    int size = instance.historyInput.size();

                    if (index >= size) {
                        index = size - 1;
                    }
                    index = Math.max(index, 0);

                    input.setText(dataArray[index]);


                } catch (Exception ex) {
                    System.out.println("info error 获取历史记录失败" + ex);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                    ctrlPressed = false;
                }
            }
        };
    }
}