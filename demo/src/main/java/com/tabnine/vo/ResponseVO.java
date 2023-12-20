package tabCompletion.vo;

import java.util.List;
import java.util.Map;

public class ResponseVO {

    private List<ChoiceVO> choices;
    private String id;
//    private String model;
//    private int prompt_tokens;
//    private String start_time;
//    private String end_time;
//    private int cost_time;
//    private String model_start_time;
//    private String model_end_time;
//    private int model_cost_time;
//    private int max_token;
//    private String prompt;
//    private boolean is_same = true;
//    private List<ChoiceVO> model_choices;
    private Map<String, Object> server_extra_kwargs = null;
//    private Map<String, Object> system_plugin_configs;
//
//    public Map<String, Object> getServer_extra_kwargs(){
//        return server_extra_kwargs;
//    }
//
//    public boolean getIs_same() {
//        return is_same;
//    }
//
//    public Map<String, Object> getSystem_plugin_configs() {
//        return system_plugin_configs;
//    }
//
//    public List<ChoiceVO> getModel_choices() {
//        return model_choices;
//    }
//
//    public String getPrompt() {
//        return prompt;
//    }
    public List<ChoiceVO> getChoices() {
        return choices;
    }
//
    public String getId(){
        return this.id;
    }
//
//    public String getModel(){
//        return this.model;
//    }
//
//    public void setChoices(List<ChoiceVO> choices) {
//        this.choices = choices;
//    }
//
//    public int getPrompt_tokens() {
//        return prompt_tokens;
//    }
//
//    public String getStart_time() {
//        return start_time;
//    }
//
//    public String getEnd_time() {
//        return end_time;
//    }
//
//    public int getCost_time() {
//        return cost_time;
//    }
//
//    public String getModel_start_time() {
//        return model_start_time;
//    }
//
//    public String getModel_end_time() {
//        return model_end_time;
//    }
//
//    public int getModel_cost_time() {
//        return model_cost_time;
//    }
//
//    public int getMax_token() {
//        return max_token;
//    }

    @Override
    public String toString() {
        return "ResponseVO{" +
                "choices=" + choices +
                "server_extra_kwargs=" + server_extra_kwargs +
                '}';
    }
}