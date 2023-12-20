package tabCompletion.vo;


public class ExtraVO {
    private int prompt_tokens;
    private String start_time;
    private String end_time;
    private int cost_time;
    private String model_start_time;
    private String model_end_time;
    private String trigger_mode;
    private String file_project_path;
    private int model_cost_time;
    private int max_token;
    private boolean is_same;
    private String model_completions_text;

    public ExtraVO(int prompt_tokens, String start_time, String end_time, int cost_time, String model_start_time,
                   String model_end_time, int model_cost_time, int max_token,String trigger_mode, String file_project_path,
                   boolean is_same, String model_completions_text) {
        this.prompt_tokens = prompt_tokens;
        this.start_time = start_time;
        this.end_time = end_time;
        this.cost_time = cost_time;
        this.model_start_time = model_start_time;
        this.model_end_time = model_end_time;
        this.model_cost_time = model_cost_time;
        this.max_token = max_token;
        this.trigger_mode = trigger_mode;
        this.file_project_path = file_project_path;
        this.is_same = is_same;
        this.model_completions_text = model_completions_text;
    }
}
