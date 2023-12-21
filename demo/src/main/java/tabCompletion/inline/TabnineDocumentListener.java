package tabCompletion.inline;

import static com.intellij.openapi.editor.EditorModificationUtil.checkModificationAllowed;
import static tabCompletion.general.DependencyContainer.instanceOfSuggestionsModeService;
import static tabCompletion.general.DependencyContainer.singletonOfInlineCompletionHandler;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.event.BulkAwareDocumentListener;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.wm.IdeFocusManager;
import tabCompletion.binary.requests.notifications.shown.SuggestionDroppedReason;
import tabCompletion.capabilities.SuggestionsModeService;
import tabCompletion.general.CompletionsEventSender;
import tabCompletion.general.DependencyContainer;
import tabCompletion.general.EditorUtils;
import tabCompletion.inline.*;
import tabCompletion.inline.CompletionPreview;
import tabCompletion.inline.InlineCompletionHandler;
import tabCompletion.prediction.TabNineCompletion;
import tabCompletion.state.CompletionsState;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TabnineDocumentListener implements BulkAwareDocumentListener {
  private final InlineCompletionHandler handler = singletonOfInlineCompletionHandler();
  private final SuggestionsModeService suggestionsModeService = instanceOfSuggestionsModeService();
  private final CompletionsEventSender completionsEventSender =
      DependencyContainer.instanceOfCompletionsEventSender();

  Timer timer;
  @Override
  public void documentChangedNonBulk(@NotNull DocumentEvent event) {
    if (!CompletionsState.INSTANCE.isCompletionsEnabled()) {
      return;
    }

    Document document = event.getDocument();
    Editor editor = getActiveEditor(document);

    if (editor == null || !EditorUtils.isMainEditor(editor)) {
      return;
    }

    TabNineCompletion lastShownCompletion = tabCompletion.inline.CompletionPreview.getCurrentCompletion(editor);

    CompletionPreview.clear(editor);

    int offset = event.getOffset() + event.getNewLength();

    if (shouldIgnoreChange(event, editor, offset, lastShownCompletion)) {
      InlineCompletionCache.getInstance().clear(editor);
      System.out.println("------------shouldIgnoreChange------------");
      return;
    }
    System.out.println("------------documentChangedNonBulk------------");
    System.out.println(event.getOffset());
    System.out.println(event.getNewLength());
    System.out.println(lastShownCompletion);
    System.out.println("------------documentChangedNonBulk------------");

    if (timer != null) {
      timer.cancel();
      System.out.println("completion 定时器已存在");
      timer = null;
    }

    // 创建定时器
    timer = new Timer();
    // 创建定时任务
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        timer = null;
        SwingUtilities.invokeLater(() -> {
          System.out.println("completion 定时器开始执行");
          handler.retrieveAndShowCompletion(
                  editor,
                  offset,
                  lastShownCompletion,
                  event.getNewFragment().toString(),
                  new DefaultCompletionAdjustment());

        });

      }
    };

    // 延迟1秒后执行任务
    timer.schedule(task, 500);



  }

  private boolean shouldIgnoreChange(
      DocumentEvent event, Editor editor, int offset, TabNineCompletion lastShownCompletion) {
    Document document = event.getDocument();

    if (!suggestionsModeService.getSuggestionMode().isInlineEnabled()) {
      return true;
    }

    if (event.getNewLength() < 1) {
      completionsEventSender.sendSuggestionDropped(
          editor, lastShownCompletion, SuggestionDroppedReason.TextDeletion);
      return true;
    }

    if (!editor.getEditorKind().equals(EditorKind.MAIN_EDITOR)
        && !ApplicationManager.getApplication().isUnitTestMode()) {
      return true;
    }

    if (!checkModificationAllowed(editor) || document.getRangeGuard(offset, offset) != null) {
      document.fireReadOnlyModificationAttempt();

      return true;
    }

    return !CompletionUtils.isValidDocumentChange(document, offset, event.getOffset());
  }

  @Nullable
  private static Editor getActiveEditor(@NotNull Document document) {
    if (!ApplicationManager.getApplication().isDispatchThread()) {
      return null;
    }

    Component focusOwner = IdeFocusManager.getGlobalInstance().getFocusOwner();
    DataContext dataContext = DataManager.getInstance().getDataContext(focusOwner);
    // ignore caret placing when exiting
    Editor activeEditor =
        ApplicationManager.getApplication().isDisposed()
            ? null
            : CommonDataKeys.EDITOR.getData(dataContext);

    if (activeEditor != null && activeEditor.getDocument() != document) {
      activeEditor = null;
    }

    return activeEditor;
  }
}
