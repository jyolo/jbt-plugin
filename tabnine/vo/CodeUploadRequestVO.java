package com.tabnine.vo;

import java.util.Map;

public class CodeUploadRequestVO extends RequestVO{

    String response_id;
    String languageId;
    String prompts;
    String code_completions_text;
    Boolean is_code_completion;
    String ide;
    String current_model;
    ExtraVO extra_kwargs;
    private Map<String, Object> server_extra_kwargs;

    public CodeUploadRequestVO() {
    }

    @Override
    public String toString() {
        return "RequestVO{" +
                "response_id='" + response_id + '\'' +
                ", languageId=" + languageId +
                ", is_code_completion=" + is_code_completion +
                ", prompts=" + prompts +
                ", code_completions_text=" + code_completions_text +
                ", ide='" + ide + '\'' +
                ", extra_kwargs='" + extra_kwargs + '\'' +
                ", server_extra_kwargs='" + server_extra_kwargs + '\'' +
                '}';
    }

    public void setExtra(ExtraVO extra) {
        this.extra_kwargs = extra;
    }


    public CodeUploadRequestVO(String response_id, String languageId, String prompts, String code_completions_text, boolean is_code_completion, String ide, String current_model) {
        this.response_id = response_id;
        this.languageId = languageId;
        this.prompts = prompts;
        this.code_completions_text = code_completions_text;
        this.is_code_completion = is_code_completion;
        this.ide = ide;
        this.current_model = current_model;
    }

    public void setServer_extra_kwargs(Map<String, Object> server_extra_kwargs) {
        this.server_extra_kwargs = server_extra_kwargs;
    }

    public void setResponse_id(String response_id) {
        this.response_id = response_id;
    }

    public String getResponse_id() {return this.response_id;}

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public void setCode_completions_text(String code_completions_text) {
        this.code_completions_text = code_completions_text;
    }

    public void setIs_code_completion(Boolean is_code_completion) {
        this.is_code_completion = is_code_completion;
    }

    public void setIde(String ide) {
        this.ide = ide;
    }

    public void setPrompts(String prompts) {
        this.prompts = prompts;
    }

    public void setCurrent_model(String current_model) {
        this.current_model = current_model;
    }
}
