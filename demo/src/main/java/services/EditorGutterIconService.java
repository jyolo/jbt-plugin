package services;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.EditorColors.SELECTION_BACKGROUND_COLOR;
import static com.intellij.openapi.editor.colors.EditorColors.SELECTION_FOREGROUND_COLOR;

public class EditorGutterIconService {

    public Project project;
    public Timer timer;
    public int selectStart = -1;
    public int selectEnd = -1;
    public String selectFilePath ;
    public Map<String, Boolean> selectFileColse = new HashMap<>();

    public int timerMaxTime = 1800; // 3分钟


    public EditorGutterIconService(Project project){
        this.project = project;
    }
    public static EditorGutterIconService getInstance(@NotNull Project project) {
        return project.getService(EditorGutterIconService.class);
    }

    public void setSelectStart(int selectStart) {
        this.selectStart = selectStart;
    }
    public void setSelectEnd(int selectEnd){
        this.selectEnd = selectEnd;
    }

    public void setSelectFilePath(String filePath){
        this.selectFilePath = filePath;
        this.selectFileColse.put(filePath, false);
    }

    public void setSelectFileColse(String filePath, Boolean isClose){
        if(this.selectFileColse.containsKey(filePath)){
            this.selectFileColse.put(filePath, isClose);
        }
    }

    public void startShow(){
        System.out.println("++++++++++++++++==startShow");
        timer = new Timer(100, new ActionListener() {
            private int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {

                ApplicationManager.getApplication().invokeLater(() -> {

                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if(editor == null){
                        System.out.println("startShow editor is null stoped");
                        ((Timer) e.getSource()).stop();
                        return;
                    }

                    if(count >= timerMaxTime){
                        RangeHighlighter @NotNull [] oldHighlighters = editor.getMarkupModel().getAllHighlighters();
                        if(oldHighlighters.length > 0){
                            editor.getMarkupModel().removeAllHighlighters();
                        }
                        System.out.println("startShow editor is null stoped");
                        ((Timer) e.getSource()).stop();
                        return;
                    }

                    RangeHighlighter @NotNull [] oldHighlighters = editor.getMarkupModel().getAllHighlighters();
                    if(oldHighlighters.length > 0){
                        editor.getMarkupModel().removeAllHighlighters();
                    }
                    // 当前选中的文件
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    VirtualFile virtualFile = fileEditorManager.getSelectedFiles()[0];
                    String filePath = virtualFile.getPath();

                    if(selectStart != -1 && filePath.equals(selectFilePath)){// 行号旁边的 旋转图标
                        int lineNumber = editor.getDocument().getLineNumber(selectStart);
                        RangeHighlighter highlighter = editor.getMarkupModel().addLineHighlighter(lineNumber, 0, null);
                        MyGutterIconRenderer myGutterIconRenderer = new MyGutterIconRenderer(count, ((Timer) e.getSource()), editor, highlighter);
                        highlighter.setGutterIconRenderer(myGutterIconRenderer);

                        // 代码选中区域保持选中的高亮状态
                        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
                        TextAttributesKey selectionAttributesKey = TextAttributesKey.createTextAttributesKey("SELECTION_ATTRIBUTES");
                        // 这里不能用全局scheme获取TextAttributes, 会影响全局样式
                        TextAttributes selectionAttributes = new TextAttributes();
                        scheme.setAttributes(selectionAttributesKey, selectionAttributes);
                        selectionAttributes.setBackgroundColor(scheme.getColor(SELECTION_BACKGROUND_COLOR));
                        selectionAttributes.setForegroundColor(scheme.getColor(SELECTION_FOREGROUND_COLOR));
                        editor.getMarkupModel().addRangeHighlighter(
                                selectStart,
                                selectEnd,
                                HighlighterLayer.SELECTION - 1,
                                selectionAttributes,
                                HighlighterTargetArea.EXACT_RANGE
                        );
                    }
                    count++;
                });

            }
        });
        timer.start();
    }
    public void stopShow(){
        int delay = 100 ; //1ms

        Timer checktimer = new Timer(delay, new ActionListener() {
            private int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if(timer != null && timer.isRunning()){
                    ApplicationManager.getApplication().invokeLater(() -> {
                        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                        // 当前选中的文件
                        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                        VirtualFile virtualFile = fileEditorManager.getSelectedFiles()[0];
                        String filePath = virtualFile.getPath();

                        if(editor != null && filePath.equals(selectFilePath)){
                            System.out.println("----------stopShow in normal status ----------");
                            RangeHighlighter @NotNull [] oldHighlighters = editor.getMarkupModel().getAllHighlighters();
                            if(oldHighlighters.length > 0){
                                editor.getMarkupModel().removeAllHighlighters();
                            }
                            ((EditorEx)editor).getGutterComponentEx().updateUI();
                            timer.stop();
                            ((Timer) e.getSource()).stop();
                        }

                    });
                }

                // 文件被关闭的时候 停止定时器
                if(selectFileColse != null && selectFileColse.get(selectFilePath)){
                    timer.stop();
                    ((Timer) e.getSource()).stop();
                    System.out.println("----------stopShow file is cloesd ----------");
                }
                // 超时
                if(count >= timerMaxTime){
                    if(timer!=null){
                        timer.stop();
                    }
                    ((Timer) e.getSource()).stop();
                    System.out.println("----------stopShow over maxtime  ----------");
                }

                count++ ;
            }
        });
        checktimer.start();

    }
}

