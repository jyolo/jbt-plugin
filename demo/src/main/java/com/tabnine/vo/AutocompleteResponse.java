package com.tabnine.vo;

import com.tabnineCommon.binary.BinaryResponse;

import java.util.Arrays;

public class AutocompleteResponse implements BinaryResponse {
    public String old_prefix;
    public ResultEntry[] results;
    public String[] user_message;
    public boolean is_locked;
    public String response_id;
    public String after_code;

    public String getAfter_code(){
        return after_code;
    }

    public void setAfter_code(String after_code){
        this.after_code = after_code;
    }

    public String getResponse_id(){
        return this.response_id;
    }

    public void setResponse_id(String response_id){
        this.response_id = response_id;
    }

    public String getOld_prefix() {
        return old_prefix;
    }

    public void setOld_prefix(String old_prefix) {
        this.old_prefix = old_prefix;
    }

    public ResultEntry[] getResults() {
        return results;
    }

    public void setResults(ResultEntry[] results) {
        this.results = results;
    }

    public String getNewPrefix() {
        if (results == null || results.length == 0) {
            return "";
        } else {
            return results[0].new_prefix;
        }}

    public String[] getUser_message() {
        return user_message;
    }

    public void setUser_message(String[] user_message) {
        this.user_message = user_message;
    }

    public boolean isIs_locked() {
        return is_locked;
    }

    public void setIs_locked(boolean is_locked) {
        this.is_locked = is_locked;
    }

    @Override
    public String toString() {
        return "AutocompleteResponse{" +
                "old_prefix='" + old_prefix + '\'' +
                ", results=" + Arrays.toString(results) +
                ", user_message=" + Arrays.toString(user_message) +
                ", is_locked=" + is_locked +
                '}';
    }
}
