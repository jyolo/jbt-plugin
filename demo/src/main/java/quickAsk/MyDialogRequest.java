package quickAsk;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.util;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.obiscr.chatgpt.core.TokenManager;
import setting.AppSettingsState;

import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


public class MyDialogRequest {

    public Project project;
    public MyDialogInput myDialogInput;

    public HttpURLConnection connection;
    public String controllerAction = "scribe";
    public Integer maxConnectionTime = (1000 * 60) * 3; // 三分钟兜底超时


    public MyDialogRequest(Project project, MyDialogInput myDialogInput){
        this.project = project;
        this.myDialogInput = myDialogInput;
    }

    public void doRequest(Timer loadingTimer, String inputContent, String selectedText){
        System.out.println("quickAsk start request--------");
        StringBuilder response = new StringBuilder();
        int responseCode = 0;
        try {
            AppSettingsState settings = AppSettingsState.getInstance();
            String qianLiuServerUrl = settings.getQianLiuServerUrl();
            String url = qianLiuServerUrl + "/api/v2/completion";
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(maxConnectionTime);
            connection.setConnectTimeout(maxConnectionTime);

            Map<String, String> headers = TokenManager.getInstance().getGPT35TurboHeaders();
            headers.put("action", controllerAction);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            JSONObject params = new JSONObject();
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            VirtualFile virtualFile = fileEditorManager.getSelectedFiles()[0];
            String filePath = virtualFile.getPath();
            String languageId = util.getLanguageByFilePath(filePath);
            String gitPath = util.getGitPathByFilePath(project);
            params.put("language", languageId);
            params.put("code", selectedText);
            params.put("prompt", inputContent);
            params.put("custom_instructions", "");
            params.put("action", controllerAction);
            params.put("stream", false);
            java.util.List<String> collection_list = new ArrayList<>();
            collection_list.add("sase");
            collection_list.add("idux");
            params.put("collection_list", collection_list);
            params.put("is_carry_code", true);
            params.put("git_path", gitPath);
            String requestBody = JSON.toJSONString(params);
            // 还未连接，进行连接操作
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeChars(requestBody);
            wr.flush();
            wr.close();
            // 获取响应
            responseCode = connection.getResponseCode();
            InputStream inputStream = responseCode == HttpURLConnection.HTTP_OK ? connection.getInputStream() : connection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            inputStream.close();
            connection.disconnect();

            if(response.toString().trim().equals("Unauthorized")){
                JSONObject data = new JSONObject();
                data.put("error_code", 100002);
                afterRequestCallBack(data, true);
            }else{
                JSONObject responseJson = JSONObject.parseObject(response.toString());
                afterRequestCallBack(responseJson, false);
            }

        }catch (SocketTimeoutException e) {
            // 处理连接超时错误
            System.out.println("----SocketTimeoutException----");
            JSONObject data = new JSONObject();
            data.put("error_code", responseCode);
            data.put("message", "请求超时，请检查后重试");
            afterRequestCallBack(data, true);

        }catch (ConnectException e) {
            System.out.println("链接断开:----ConnectException----");
            // 处理连接断开错误
            JSONObject data = new JSONObject();
            data.put("error_code", responseCode);
            data.put("message", "链接断开");
            afterRequestCallBack(data, true);

        }catch (IOException e){
            System.out.println("链接断开:----IOException----");
            // 处理连接断开错误
            JSONObject data = new JSONObject();
            data.put("error_code", responseCode);
            data.put("message", e.getMessage());
            if(e.getMessage() == "Socket closed" && myDialogInput.sendReqeustIsRunning != false){
                data.put("message", "链接已断开");
                afterRequestCallBack(data, false);
            }
        }catch (Exception e){
            System.out.println("链接断开:----Exception----" + responseCode);
            if(responseCode >= 500){
                JSONObject data = new JSONObject();
                data.put("error_code", responseCode);
                data.put("message", "请求失败，服务或网络异常，请稍后再试。");
                afterRequestCallBack(data, true);
            }

            System.out.println("quickAsk  request error: " + e.getMessage() + " response code" + responseCode +"; response: " + response.toString());
        }finally {
            System.out.println("finally:------stop timer and Request-------");
            loadingTimer.stop();
            stopRequest();
        }

    }


    public void afterRequestCallBack(JSONObject data, Boolean isErrorMsg){
        myDialogInput.afterRequestCallBack(data, isErrorMsg);
    }

    public void stopRequest(){
        if (connection != null) {
            connection.disconnect();
            System.out.println("Connection has been disconnected.");
            connection = null;
        }
    }
}