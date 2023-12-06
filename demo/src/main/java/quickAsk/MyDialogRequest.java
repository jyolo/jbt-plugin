package quickAsk;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.RequestHeaders;
import com.common.tools.OkHttpRequest;
import com.common.tools.TaskQueue;
import com.common.util;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import settings.Constants;

import javax.swing.*;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MyDialogRequest {

    public Project project;
    public MyDialogInput myDialogInput;
    public Editor editor;
    public Document document;
    public HttpURLConnection connection;
    public String controllerAction = "scribe";
    public Integer maxConnectionTime = (1000 * 60) * 3; // 三分钟兜底超时


    public MyDialogRequest(Project project, MyDialogInput myDialogInput){
        this.project = project;
        this.myDialogInput = myDialogInput;
        this.editor = myDialogInput.editor;
        this.document = editor.getDocument();
    }

    public void doRequest(Timer loadingTimer, String inputContent, String selectedText){
        System.out.println("quickAsk start request--------");
        StringBuilder response = new StringBuilder();
        int responseCode = 0;
        String url = Constants.SERVER_HOST + "/v1/completion";
        Boolean stream = true;
        try {
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
            params.put("stream", stream);
            java.util.List<String> collection_list = new ArrayList<>();
            collection_list.add("sase");
            collection_list.add("idux");
            params.put("collection_list", collection_list);
            params.put("is_carry_code", true);
            params.put("git_path", gitPath);

            String requestBody = JSON.toJSONString(params);

            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setReadTimeout(maxConnectionTime);
            connection.setConnectTimeout(maxConnectionTime);

            Map<String, String> headers = RequestHeaders.getInstance().getRequestHeaders();
            headers.put("action", controllerAction);
            headers.put("service", "llamacpp");
            headers.put("model", "llama-2-13b-chat.Q4_0.gguf");
//            headers.put("service", "openai");
//            headers.put("model", "gpt-3.5-turbo-instruct");
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            // 还未连接，进行连接操作
            connection.setDoOutput(true);
            try (
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"))
            ) {
                writer.write(requestBody);
                writer.flush();
                System.out.println("Request sent successfully111");
            }
            System.out.println("Request sent successfully222");
            // 获取响应流
            InputStream inputStream = connection.getInputStream();
            System.out.println("Request sent successfully3333");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            TaskQueue taskQueue = new TaskQueue();
            System.out.println("Request sent successfully444");
            while ((line = reader.readLine()) != null) {
                if(stream){
                    String streamLine = line;
                    taskQueue.addTask(()->{
                        streamWriteToDocument(streamLine);
                    });
                }else{
                    response.append(line);
                }
            }

            // 获取响应
            responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if(HttpURLConnection.HTTP_OK != 200){
                inputStream = connection.getErrorStream();
            }


//            reader.close();
//            inputStream.close();
//            connection.disconnect();
//
//            JSONObject dict = JSONObject.parse(String.valueOf(response));
//            if(dict == null){
//               System.out.println(responseCode);
//               System.out.println(response);
//               return;
//            }
//            String text = dict.getString("data");
//            SwingUtilities.invokeLater(()->{
//                ApplicationManager.getApplication().invokeLaterOnWriteThread(() -> {
//                    // 获取当前光标位置
//                    Caret caret = editor.getCaretModel().getPrimaryCaret();
//                    int offset = caret.getOffset();
//                    // 在光标位置插入代码
//                    WriteCommandAction.runWriteCommandAction(project, () -> {
//                        document.insertString(offset, text);
//                        // 获取插入后的新光标位置
//                        int newOffset = offset + text.length();
//                        // 设置新光标位置
//                        caret.moveToOffset(newOffset);
//                    });
//                });
//            });
//            if(response.toString().trim().equals("Unauthorized")){
//                JSONObject data = new JSONObject();
//                data.put("error_code", 100002);
//                afterRequestCallBack(data, true);
//            }else{
//                JSONObject responseJson = JSONObject.parseObject(response.toString());
//                afterRequestCallBack(responseJson, false);
//            }

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
            e.printStackTrace();
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

    public void streamWriteToDocument(String streamContent){
        if (project != null && editor != null && streamContent != null) {
            JSONObject dict = null;
            try{
                dict = JSONObject.parse(streamContent);
            } catch (Exception e){
                dict = null;
            }
            if(dict == null){
                return;
            }
            String text = dict.getString("data");
            if(text == null){
                return;
            }
            SwingUtilities.invokeLater(()->{
                ApplicationManager.getApplication().invokeLater(() -> {
                    // 获取当前光标位置
                    Caret caret = editor.getCaretModel().getPrimaryCaret();
                    int offset = caret.getOffset();
                    // 在光标位置插入代码
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        document.insertString(offset, text);
                        // 获取插入后的新光标位置
                        int newOffset = offset + text.length();
                        // 设置新光标位置
                        caret.moveToOffset(newOffset);
                    });
                });

            });
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