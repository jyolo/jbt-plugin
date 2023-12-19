package tabCompletion.statusBar;

import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import tabCompletion.binary.requests.statusBar.StatusBarInteractionRequest;
import tabCompletion.binary.BinaryRequestFacade;
import tabCompletion.general.DependencyContainer;
import org.jetbrains.annotations.NotNull;

public class StatusBarPopupListener implements JBPopupListener {
  private final BinaryRequestFacade binaryRequestFacade =
      DependencyContainer.instanceOfBinaryRequestFacade();

  @Override
  public void beforeShown(@NotNull LightweightWindowEvent event) {
//    binaryRequestFacade.executeRequest(new StatusBarInteractionRequest());
  }
}
