package com.qianliuAiUi.ideActions.editor;

import com.alibaba.fastjson2.JSONObject;
import com.common.util;
import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.qianliuAiUi.JsBridgeService;
import com.qianliuAiUi.statusBar.AIReviewStatusBarWidget;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;


public class SchemeChangeListener implements EditorColorsListener {

    @Override
    public void globalSchemeChange(@Nullable EditorColorsScheme scheme) {
        Map<String, Object> params = util.getIdeThemeColor(new JSONObject());

        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        // 切换主题的时候 更新 AIReviewStatusBarWidget
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        AIReviewStatusBarWidget widget = new AIReviewStatusBarWidget(project);
        statusBar.updateWidget(widget.ID());


        new JsBridgeService(project).callActionFromIde("ide.changeTheme", params);
        new JsBridgeService(project).callActionFromIde("editor.changeTheme", util.getIdeThemeStyle(new JSONObject()));

    }
}