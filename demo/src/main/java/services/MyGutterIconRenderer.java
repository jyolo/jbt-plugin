package services;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.containers.ContainerUtil;
import com.common.ChatGPTIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class MyGutterIconRenderer extends GutterIconRenderer {
    List<Icon> icons = ContainerUtil.immutableList(
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading1.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading2.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading3.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading4.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading5.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading6.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading7.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading8.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading9.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading10.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading11.svg", ChatGPTIcons.class),
            IconLoader.getIcon("/icons/qianliu_ai_logo_loading12.svg", ChatGPTIcons.class)
    );

    Integer loopNum;
    Timer timer;
    Editor editor;
    RangeHighlighter highlighter;
    public MyGutterIconRenderer(Integer loopNum, Timer timer, Editor editor, RangeHighlighter highlighter){
        this.loopNum = loopNum;
        this.timer = timer;
        this.editor = editor;
        this.highlighter = highlighter;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public @NotNull Icon getIcon() {
        this.loopNum %= icons.size(); // 取模icons的长度，保证 count 始终在 icons的长度 的范围内
        return icons.get(this.loopNum);
    }

    @Override
    public @Nullable AnAction getClickAction() {
//        return super.getClickAction();
//        System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
//        timer.stop();
//        editor.getMarkupModel().removeHighlighter(highlighter);
//        ((EditorEx)editor).getGutterComponentEx().updateUI();
        return super.getClickAction();
    }


}
