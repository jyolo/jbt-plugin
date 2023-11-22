package com.obiscr.tabnine.binary.requests.autocomplete;

import com.obiscr.tabnine.general.CompletionKind;
import com.obiscr.tabnine.intellij.completions.Completion;

public class ResultEntry implements Completion {
    public String new_prefix;
    public String old_suffix;
    public String new_suffix;
    public CompletionMetadata completion_metadata;

    @Override
    public boolean isSnippet() {
        if (this.completion_metadata == null) {
            return false;
        }

        return this.completion_metadata.getCompletion_kind() == CompletionKind.Snippet;
    }

    public String getNew_prefix() {
        return new_prefix;
    }

    public void setNew_prefix(String new_prefix) {
        this.new_prefix = new_prefix;
    }

    public String getOld_suffix() {
        return old_suffix;
    }

    public void setOld_suffix(String old_suffix) {
        this.old_suffix = old_suffix;
    }

    public String getNew_suffix() {
        return new_suffix;
    }

    public void setNew_suffix(String new_suffix) {
        this.new_suffix = new_suffix;
    }

    public CompletionMetadata getCompletion_metadata() {
        return completion_metadata;
    }

    public void setCompletion_metadata(CompletionMetadata completion_metadata) {
        this.completion_metadata = completion_metadata;
    }

    @Override
    public String toString() {
        return "ResultEntry{" +
                "new_prefix='" + new_prefix + '\'' +
                ", old_suffix='" + old_suffix + '\'' +
                ", new_suffix='" + new_suffix + '\'' +
                ", completion_metadata=" + completion_metadata +
                '}';
    }
}
