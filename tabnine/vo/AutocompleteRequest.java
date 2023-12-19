package com.tabnine.vo;

import static java.util.Collections.singletonMap;


import com.google.gson.annotations.SerializedName;
import com.tabnineCommon.binary.BinaryRequest;
import org.jetbrains.annotations.Nullable;

public class AutocompleteRequest implements BinaryRequest {
    public String before;
    public String after;
    public String filename;
    public String trigger_mode;
    public String completionAdjustment_hash_code ;
    public String languageId = "";
    public String file_project_path = "";
    public String git_path = "";


    @SerializedName(value = "region_includes_beginning")
    public boolean regionIncludesBeginning;

    @SerializedName(value = "region_includes_end")
    public boolean regionIncludesEnd;

    @SerializedName(value = "max_num_results")
    public int maxResults;

    public int offset;
    public int line;
    public int character;

    @Nullable public Integer indentation_size;

    public Class<AutocompleteResponse> response() {
        return AutocompleteResponse.class;
    }

    @Override
    public Object serialize() {
        return singletonMap("Autocomplete", this);
    }

    public boolean validate(AutocompleteResponse response) {
        return this.before.endsWith(response.old_prefix);
    }

    @Override
    public String toString() {
        return "AutocompleteRequest{" +
                "before='" + before + '\'' +
                ", after='" + after + '\'' +
                ", filename='" + filename + '\'' +
                ", regionIncludesBeginning=" + regionIncludesBeginning +
                ", regionIncludesEnd=" + regionIncludesEnd +
                ", maxResults=" + maxResults +
                ", offset=" + offset +
                ", line=" + line +
                ", character=" + character +
                ", indentation_size=" + indentation_size +
                ", trigger_mode=" + trigger_mode +
                ", file_project_path=" + file_project_path +
                ", git_path=" + git_path +
                '}';
    }
}
