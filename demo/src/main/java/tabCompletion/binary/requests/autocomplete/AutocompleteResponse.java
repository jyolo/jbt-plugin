package tabCompletion.binary.requests.autocomplete;

import tabCompletion.binary.BinaryResponse;
import tabCompletion.binary.requests.autocomplete.ResultEntry;

public class AutocompleteResponse implements BinaryResponse {
  public String old_prefix;
  public ResultEntry[] results;
  public String[] user_message;
  public boolean is_locked;
}
