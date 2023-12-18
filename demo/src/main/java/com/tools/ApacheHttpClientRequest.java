package com.tools;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.Map;

public class ApacheHttpClientRequest {
    private HttpClient httpClient;
    private String requestUrl;
    private StringEntity requestBody;
    private Map<String, String> requestHeaders;

    public ApacheHttpClientRequest(String requestUrl, Map<String, String> requestHeaders, Map<String, Object> requestData) {
        this.httpClient = HttpClients.createDefault();
        this.requestUrl = requestUrl;
        this.requestBody = makeRequestJsonBody(requestData);
        this.requestHeaders = requestHeaders;
    }

    private StringEntity makeRequestJsonBody(Map<String, Object> requestData) {
        JSONObject jsonObject = new JSONObject(requestData);
        String jsonBodyString = jsonObject.toString();
        return new StringEntity(jsonBodyString, "UTF-8");
    }

    public ApacheHttpClientRequest getResponse() {

        HttpPost httpPost = new HttpPost(this.requestUrl);
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(requestBody);
        ;
        sendAndParseResponse(httpPost);
        return this;
    }

    private void sendAndParseResponse(HttpPost httpPost) {
        try {

            HttpResponse response = httpClient.execute(httpPost);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // 获取输入流
//                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    System.out.println(line);
//                }
//
//                EntityUtils.consume(entity);
            } else {
                System.out.println("Response entity is null.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ApacheHttpClientRequest getStreamResponse() {
        HttpPost httpPost = new HttpPost(this.requestUrl);
        httpPost.setEntity(requestBody);
        for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
            httpPost.addHeader(entry.getKey(), entry.getValue());
        }
        sendAndReadStream(httpPost);
        return this;
    }

    private void sendAndReadStream(HttpPost httpPost) {
        try {
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 获取字符流
                Reader source = new InputStreamReader(entity.getContent());
                BufferedReader reader = new BufferedReader(source, 1);
                reader.mark(1);
//                 使用 BufferedReader 逐行读取
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("line-----------------------");
                    System.out.println(line);
                    System.out.println("line-----------------------");
                }
                source.close();
            } else {
                System.out.println("Response entity is null.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
