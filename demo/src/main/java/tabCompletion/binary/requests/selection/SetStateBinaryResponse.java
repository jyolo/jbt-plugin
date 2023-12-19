package tabCompletion.binary.requests.selection;

import tabCompletion.binary.BinaryResponse;

public class SetStateBinaryResponse implements BinaryResponse {
  private String result;

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }
}
