package com.common;

public class ResultEntry  {
    public String new_prefix;
    public String old_suffix;
    public String new_suffix;

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



    @Override
    public String toString() {
        return "ResultEntry{" +
                "new_prefix='" + new_prefix + '\'' +
                ", old_suffix='" + old_suffix + '\'' +
                ", new_suffix='" + new_suffix + '\'' +
                '}';
    }
}
