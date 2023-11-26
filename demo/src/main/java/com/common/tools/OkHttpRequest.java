package com.common.tools;

import com.alibaba.fastjson2.JSONObject;
import okhttp3.*;
import okio.BufferedSource;

import java.io.*;
import java.util.List;
import java.util.Map;

public class OkHttpRequest {
    private OkHttpClient client;
    private String requestUrl;
    private RequestBody requestBody;
    private Map<String, String> requestHeaders;

    public OkHttpRequest(String requestUrl, Map<String, String> requestHeaders, Map<String, Object> requestData) {
        this.client = new OkHttpClient();
        this.requestUrl = requestUrl;
        this.requestBody = makeRequestJsonBody(requestData);
//        makeRequest(requestHeaders);
        this.requestHeaders = requestHeaders;
    }

    private RequestBody makeRequestFormBody(Map<String, Object> requestData) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : requestData.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            formBodyBuilder.add(key, String.valueOf(value));
        }
        return formBodyBuilder.build();
    }

    private RequestBody makeRequestJsonBody(Map<String, Object> requestData) {
        JSONObject jsonObject = new JSONObject(requestData);
        String jsonBodyString = jsonObject.toString();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonBodyString);
        return requestBody;
    }
    public OkHttpRequest getResponse() {
        Request request = new Request.Builder()
                .url(this.requestUrl)
                .post(requestBody)
                .build();
        sendAndParseResponse(request);
        return this;
    }
    private void sendAndParseResponse(Request request) {
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            System.out.println("Response body: " + responseBody);
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
        }
    }

    public OkHttpRequest getStreamResponse() {
        Request request = new Request.Builder()
                .url(this.requestUrl)
                .headers(Headers.of(requestHeaders))
                .post(requestBody)
                .build();
        sendAndReadStream(request);
        return this;
    }
    private void sendAndReadStream(Request request) {
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                // 获取字符流
                Reader source = response.body().charStream();
                // 使用 BufferedReader 逐行读取
                try (BufferedReader reader = new BufferedReader(source)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                source.close();
            } else {
                System.out.println("Request failed: " + response.code() + ", " + response.message());
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
