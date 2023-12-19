package tabCompletion.binary.requests.statusBar;

import static tabCompletion.general.StaticConfig.SET_STATE_RESPONSE_RESULT_STRING;
import static java.util.Collections.singletonMap;

import tabCompletion.binary.BinaryRequest;
import tabCompletion.binary.requests.selection.SetStateBinaryResponse;
import org.jetbrains.annotations.NotNull;

public class StatusBarInteractionRequest implements BinaryRequest<SetStateBinaryResponse> {
  @Override
  public Class<SetStateBinaryResponse> response() {
    return SetStateBinaryResponse.class;
  }

  @Override
  public Object serialize() {
    return singletonMap(
        "SetState",
        singletonMap("state_type", singletonMap("State", singletonMap("state_type", "status"))));
  }

  @Override
  public boolean validate(@NotNull SetStateBinaryResponse response) {
    return SET_STATE_RESPONSE_RESULT_STRING.equals(response.getResult());
  }
}
