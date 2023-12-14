package com.tabnine.vo;

import com.common.CalculateHideScore;


public class RequestVO {

    String prompt;
    //    int max_tokens;
    double temperature;
    String model;
    String language_id;
    String trigger_mode;
    String file_project_path;
    boolean beta_mode;
    String repo;
    String user_id;
    public String git_path = "";

    public CalculateHideScore calculate_hide_score = new CalculateHideScore();

    public RequestVO() {
    }

    @Override
    public String toString() {
        return "RequestVO{" +
                "prompt='" + prompt + '\'' +
//                ", max_tokens=" + max_tokens +
                ", temperature=" + temperature +
                ", model='" + model + '\'' +
                ", language_id='" + language_id + '\'' +
                ", beta_mode='" + beta_mode + '\'' +
                ", trigger_mode='" + trigger_mode + '\'' +
                ", file_project_path='" + file_project_path + '\'' +
                ", id='" + user_id + '\'' +
                ", repo='" + repo + '\'' +
                ", git_path='" + git_path + '\'' +
                '}';
    }

    public RequestVO(String prompt, int maxTokens, double temperature, String model, String language_id, boolean beta_mode, String trigger_mode) {
        this.prompt = prompt;
//        this.max_tokens = maxTokens;
        this.temperature = temperature;
        this.model = model;
        this.language_id = language_id;
        this.trigger_mode = trigger_mode;
        this.beta_mode = beta_mode;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setRepo(String repo) {
        if (repo.contains(" ")) {
            this.repo = repo.split(" ")[0];
        } else {
            this.repo = repo;
        }
    }
    public void setGitpath(String userId) {
        this.git_path = userId;
    }
    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public void setTriggerModel(String trigger_mode) {
        this.trigger_mode = trigger_mode;
    }
    public String getTriggerModel() {
        return trigger_mode;
    }

    public String getLanguageId() {
        return language_id;
    }

    public void setFileProjectPath(String file_path){
        this.file_project_path = file_path;
    }
    public void setLanguageId(String language_id) {
        this.language_id = language_id;
    }
    public boolean getBetaMode() {
        return beta_mode;
    }

    public void setBetaMode(boolean beta_mode) {
        this.beta_mode = beta_mode;
    }
    //    public int getMaxTokens() {
//        return max_tokens;
//    }

//    public void setMaxTokens(int maxTokens) {
//        this.max_tokens = maxTokens;
//    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCalculateHideScore(CalculateHideScore calculateHideScore) {
        this.calculate_hide_score.is_whitespace_after_cursor = calculateHideScore.is_whitespace_after_cursor;
        this.calculate_hide_score.prefix = calculateHideScore.prefix;
        this.calculate_hide_score.document_length = calculateHideScore.document_length;
        this.calculate_hide_score.prompt_end_pos = calculateHideScore.prompt_end_pos;
        this.calculate_hide_score.previous_label = calculateHideScore.previous_label;
        this.calculate_hide_score.previous_label_timestamp = calculateHideScore.previous_label_timestamp;
    }

}