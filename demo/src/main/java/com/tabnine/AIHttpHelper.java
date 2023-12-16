package com.tabnine;


import com.intellij.openapi.application.ApplicationInfo;
import com.tabnine.vo.*;
import com.tabnineCommon.binary.BinaryRequest;
import com.tabnineCommon.binary.BinaryResponse;
import com.tabnineCommon.binary.requests.autocomplete.CompletionMetadata;
import settings.AppSettingsState;
import settings.Constants;
import settings.OpenAISettingsState;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

public class AIHttpHelper {

    static AppSettingsState settingsState = AppSettingsState.getInstance();
    static OpenAISettingsState instance = OpenAISettingsState.getInstance();
    static String FIM_INDICATOR = "<FILL_HERE>";

    public  <R extends BinaryResponse> R request(BinaryRequest<R> request) {
        System.out.println("6666666666666666666666666666");
        System.out.println(request);
        System.out.println(request instanceof AutocompleteRequest);
        System.out.println("6666666666666666666666666666");
        if (request instanceof AutocompleteRequest) {
//            settingsState.setInReasoning(true);
            boolean err_flag = true;
            try {
                System.out.println("77777777777777777777777777777");
                AutocompleteResponse autocompleteResponse = executeHttpRequest((AutocompleteRequest)request);
                if (autocompleteResponse == null){
//                    settingsState.setResultContent("error");
                } else if (autocompleteResponse.getNewPrefix().isEmpty()) {
//                    settingsState.setResultContent("empty");
                } else {
//                    settingsState.setResultContent("");
                }
                return (R)autocompleteResponse;
            } catch (SocketException e){
                System.out.println("多线程手动关闭连接；异常抛出： " + e);
//                settingsState.setInReasoning(true);
                err_flag = false;
                return (R)null;
            } catch (Exception e){
                System.out.println("异常抛出： " + e);
//                settingsState.setResultContent("error");
                e.printStackTrace();
                throw e;
            } finally {
                if (err_flag) {
//                    settingsState.setInReasoning(false);
                }
            }
        }
        return null;
    }

    private static AutocompleteResponse executeHttpRequest(AutocompleteRequest req) throws SocketException {
        RequestVO requestVO = new RequestVO();
//        requestVO.setMaxTokens(settingsState.getMaxTokens());
//        requestVO.setModel("fastertransformer");
        requestVO.setModel(Constants.DEFAULT_MODEL);
        requestVO.setTemperature(0.1);
        // fillModel 默认为true
        String prompt = String.format("%s%s%s", req.before, FIM_INDICATOR, req.after);
        requestVO.setPrompt(prompt);
//        if (settingsState.getFillMode()) {
//            String prompt = String.format("%s%s%s", req.before, FIM_INDICATOR, req.after);
//            requestVO.setPrompt(prompt);
//        }
//        else {
//            requestVO.setPrompt(req.before);
//        }
        requestVO.setLanguageId(req.languageId);
//        requestVO.setBetaMode(settingsState.getBetaMode());
        requestVO.setTriggerModel(req.trigger_mode);
        requestVO.setFileProjectPath(req.file_project_path);

//        requestVO.setRepo(Snippet.projectName);
//        requestVO.setUserId(getUserId());
        requestVO.setGitpath(req.git_path);
        System.out.println("requestVO:" + requestVO);

        AppSettingsState appSettingsState = AppSettingsState.getInstance();
        try {
            String old_prefix = genOldPrefix(req.before);
            String old_suffix = genOldSuffix(req.before);
            System.out.println("88888888888888888888888888888888");
            System.out.println(settingsState.getServerUrl());
            ResponseVO responseVO = JavaHttpHelper.post(genUrl(settingsState.getServerUrl(), settingsState.getEngine()), requestVO, instance.apiKey, req.completionAdjustment_hash_code);
            System.out.println("responseVO:" + responseVO);

            // 如果有返回 context_lines_limit 并且与默认设置不一致的时候, 则 MaxLines 的配置改为服务端返回的
            Map<String, Object> system_plugin_configs = responseVO.getSystem_plugin_configs();
            Double context_lines_limit = (Double) system_plugin_configs.getOrDefault("context_lines_limit", appSettingsState.getMaxLines());
            int max_context_lines = context_lines_limit.intValue();
//            if(max_context_lines != 0 && appSettingsState.getMaxLines() != max_context_lines){
//                appSettingsState.setMaxLines(max_context_lines);
//            }

            AutocompleteResponse autocompleteResponse = new AutocompleteResponse();
            autocompleteResponse.setOld_prefix(old_prefix);
            autocompleteResponse.setIs_locked(false);
            ResultEntry entry = new ResultEntry();

            String choiceText;
            if (responseVO.getChoices() != null && responseVO.getChoices().size() > 0) {
                choiceText = responseVO.getChoices().get(0).getText();
            }
            else {
                choiceText = "";
            }
            entry.setNew_prefix(genNewPrefix(old_prefix, choiceText));
            entry.setNew_suffix(genNewSuffix(choiceText));
            entry.setOld_suffix(old_suffix);
            CompletionMetadata metadata = new CompletionMetadata();
//            metadata.setOrigin(CompletionOrigin.LOCAL);
//            metadata.setDetail("5%");
//            metadata.setResponse_id(responseVO.getId());
//            metadata.setCompletion_kind(CompletionKind.Classic);
            Map<String, Object> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put("snippet_id", null);
            objectObjectHashMap.put("uer_intenl", null);
            objectObjectHashMap.put("intent_metadata", null);
            objectObjectHashMap.put("completion_index", null);
            objectObjectHashMap.put("is_cached", null);
            objectObjectHashMap.put("deprecated", null);
//            metadata.setSnippet_context(objectObjectHashMap);
            entry.setCompletion_metadata(metadata);
            autocompleteResponse.setUser_message(new String[]{"User1", "User2"});
            autocompleteResponse.setResults(new ResultEntry[]{entry});
            autocompleteResponse.setAfter_code(req.after);

            System.out.println("autocompleteResponse response_id: " + responseVO.getId());
//            System.out.println("autocompleteResponse response_text: " + responseVO.getChoices().get(0).getText());

            ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
            String ideName = applicationInfo.getVersionName();

            // 确保有数据的时候再上传数据，如果截断后没有数据，则不请求上报接口
            if (responseVO.getPrompt_tokens() > 0) {
                String model_completions_text = "";
                if (responseVO.getModel_choices() != null) {
                    model_completions_text = responseVO.getModel_choices()
                            .stream()
                            .findFirst()
                            .map(ChoiceVO::getText)
                            .orElse("");
                }

                ExtraVO extraVO = new ExtraVO(responseVO.getPrompt_tokens(), responseVO.getStart_time(),
                        responseVO.getEnd_time(), responseVO.getCost_time(), responseVO.getModel_start_time(),
                        responseVO.getModel_end_time(), responseVO.getModel_cost_time(), responseVO.getMax_token(),
                        req.trigger_mode, req.file_project_path,
                        responseVO.getIs_same(), model_completions_text);
                CodeUploadRequestVO Coderequest = new CodeUploadRequestVO();
                Coderequest.setExtra(extraVO);
                Coderequest.setIs_code_completion(false);
                Coderequest.setLanguageId(req.languageId);
                Coderequest.setResponse_id(responseVO.getId());
                Coderequest.setPrompts(responseVO.getPrompt());
                Coderequest.setIde(ideName);
                Coderequest.setCode_completions_text(choiceText);
                Coderequest.setCurrent_model(responseVO.getModel());
                if(responseVO.getServer_extra_kwargs() != null){
                    Coderequest.setServer_extra_kwargs(responseVO.getServer_extra_kwargs());
                }
                executeCodeCompletionHttpRequest(Coderequest);
            }

//            CacheUtil.cacheCompletion(autocompleteResponse);
            return autocompleteResponse;
        } catch (SocketException e) {
            throw e;
        } catch (IOException e) {
            System.out.println("AIHttpHelper executeHttpRequest with completionAdjustment : "+ req.completionAdjustment_hash_code+ " : error: " + e );
            return null;
        }
    }

    public static String executeCodeCompletionHttpRequest(CodeUploadRequestVO req)  {
        System.out.println("executeCodeCompletionHttpRequest response_id: " + req.getResponse_id());
//        System.out.println(req);
        String url = Constants.CODE_COMPLETION_UPLOAD_URL;
        try{
            int status_code = JavaHttpHelper.postCodeCompletionUpload(url, req, instance.apiKey);
            System.out.println("postCodeCompletionUpload success with code: " + status_code);
        }catch (IOException e){
            System.out.println("postCodeCompletionUpload fail error: " + e);
        }


        return "ok";
    }

    private static String genNewSuffix(String responseText) {
//        if (" ".equals(responseText) || "".equals(responseText)){
//            return "";
//        }
//        return responseText.endsWith(")") ? ")" : "";
        return "";
    }

    private static String genNewPrefix(String oldPrefix, String responseText) {
        if (responseText.trim().length() > 0){
            // 如果最后一个元素全是空字符，则移除最后一个元素，否则在接收补全后，光标错位的问题
            String[] res_arr = responseText.split("\n");
            if (res_arr.length > 0) {
                String last_item = res_arr[res_arr.length - 1];
                if (last_item.trim().isEmpty()) {
                    responseText = responseText.substring(0, responseText.lastIndexOf("\n"));
                }
            }
            return oldPrefix + responseText;
        }else{
            return "";
        }


    }

    private static String genOldSuffix(String before) {
//        return before.substring(before.length() - 1); // last suffix chat
//        String lastChat = before.substring(before.length() - 1);
//        if (" ".equals(lastChat) || "".equals(lastChat)){
//            return "";
//        }
//        if ("\n".equals(lastChat)) {
//            return lastChat;
//        }
//        String[] split = before.split("\n");
//        String[] res_arr = split[split.length - 1].trim().split(" ");
//        return res_arr[res_arr.length - 1];
        return "";
    }
    static String genUrl(String host, String engine) {
        if (null == host || "".equals(host) || null == engine || "".equals(engine)) {
            return String.format("%s/engines/%s/%s", Constants.DEFAULT_SERVER_HOST, Constants.DEFAULT_ENGINE, Constants.DEFAULT_SERVER_URI);
        }
        if (host.endsWith("/")) {
            return String.format("%sengines/%s/%s", host, engine, Constants.DEFAULT_SERVER_URI);
        }
        return String.format("%s/engines/%s/%s", host, engine, Constants.DEFAULT_SERVER_URI);
    }

    static String genOldPrefix(String before) {
//        int lastNewline = before.lastIndexOf("\n");
//        String lastLine = lastNewline >= 0 ? before.substring(lastNewline) : "";
//        boolean endsWithWhitespacesOnly = lastLine.trim().isEmpty();
        String lastChat = before.substring(before.length() - 1);
        if (" ".equals(lastChat) || "".equals(lastChat)){
            return "";
        }
        if ("\n".equals(lastChat)) {
            return lastChat;
        }
        String[] split = before.split("\n");
//    String endContent = endsWithWhitespacesOnly ? "" : split[split.length - 1];
//    String res = "".equals(endContent) ? split[split.length - 2] : endContent;
        String[] res_arr = split[split.length - 1].trim().split(" ");
        return res_arr[res_arr.length - 1];
    }
}
