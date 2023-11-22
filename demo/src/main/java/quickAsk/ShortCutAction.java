package quickAsk;

import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.fileEditor.FileEditorManager;
//import com.obiscr.tabnine.general.EditorUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ShortCutAction extends com.intellij.openapi.actionSystem.AnAction {

    private MyDialogDiff dialogWebview;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("----------------tirgger quickAsk shortCutAction----------------0");
        // 手动触发
        Editor editor = FileEditorManager.getInstance(e.getProject()).getSelectedTextEditor();
//        if (editor == null || !EditorUtils.isMainEditor(editor)) {
//            System.out.println("----------------tirgger quickAsk shortCutAction----------------1");
//            return;
//        }
        // 选中代码的结束部分光标位置
        SelectionModel selectionModel = editor.getSelectionModel();
        int selectionEnd = selectionModel.getSelectionEnd();
        System.out.println("Selection end offset: " + selectionEnd);
        // 获取光标位置
        CaretModel caretModel = editor.getCaretModel();
        int offset = caretModel.getOffset();
        System.out.println("caretModel  offset: " + offset);
        VisualPosition visualPosition = editor.offsetToVisualPosition(selectionEnd);
        // 获取光标位置所在行的第一个字符的偏移量
        editor.getDocument().getLineStartOffset(visualPosition.line);
        // 获取光标位置所在行的第一个字符的可视位置
        VisualPosition lineStartVisualPosition = new VisualPosition(visualPosition.line, 0);
        // 获取光标位置所在行第一个字符的坐标
        Point point = editor.visualPositionToXY(lineStartVisualPosition);
        // 将坐标转换为屏幕坐标
        Point screenPoint = editor.getContentComponent().getLocationOnScreen();
        screenPoint.translate(point.x, point.y);
        // 获取行高
        int lineHeight = editor.getLineHeight() + 10;

        String version = ApplicationInfo.getInstance().getFullVersion();
        System.out.println("version-----------------");
        String[] parts = version.split("\\.");
        int ideVersion = Integer.parseInt(parts[0]);

        System.out.println("----------------tirgger quickAsk shortCutAction----------------2");
        // 将Dialog的位置设置为光标下面一行的位置
        MyDialogInput dialog = new MyDialogInput();
        dialog.setEditor(editor);
        dialog.setAnActionEvent(e);
        dialog.setLocation(screenPoint.x, screenPoint.y + lineHeight);

        System.out.println(screenPoint.x);
        System.out.println(screenPoint.x);
        System.out.println(screenPoint.y + lineHeight);
        System.out.println(screenPoint.y + lineHeight);
        System.out.println("----------------tirgger quickAsk shortCutAction----------------3");


        // 开启定时器，让dialog里面的输入框获得输入的焦点
        Timer makeTextAreaFocusTimer = new Timer(200, new ActionListener() {
            private int count = 1;
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("makeTextAreaFocusTimer started dialog.input.isShowing: " + dialog.input.isShowing() +
                        " dialog.input.isFocusableis " + dialog.input.isFocusable());

                int dialogX = (int) dialog.getPeer().getLocation().getX();
                int dialogY = (int) dialog.getPeer().getLocation().getY();
                // 2020 的版本 setLocation 在弹出来之前设置 不生效，这里在弹出来后在修改坐标位置
                if(ideVersion == 2020 ){
                    if(dialogX != screenPoint.x && dialogY != screenPoint.y){
                        dialog.getPeer().setLocation(screenPoint.x, screenPoint.y + lineHeight);
                    }
                }

                if(dialog.input.isShowing() && dialog.input.isFocusable()){
                    dialog.input.requestFocus();
                    dialog.input.requestFocusInWindow();
                    System.out.println("makeTextAreaFocusTimer stop");
                    ((Timer) e.getSource()).stop(); // 停止定时器
                }
                count++;
                if (count >= 150) { // 30秒后停止定时器（300 * 200ms = 30000ms）
                    System.out.println("makeTextAreaFocusTimer stoped");
                    ((Timer) e.getSource()).stop(); // 停止定时器
                }
            }
        });
        makeTextAreaFocusTimer.start();
        dialog.show();
        if(ideVersion == 2020 ){
            dialog.getPeer().setLocation(screenPoint.x, screenPoint.y + lineHeight);
        }
    }


}

