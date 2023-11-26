package com.common.tools;

import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
        System.out.println("1111111111");
        System.out.println(this.requestHeaders);
        System.out.println("1111111111");
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
            System.out.println(response.getAllHeaders());

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                System.out.println(entity.getContent());
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
            System.out.println("22222");
            System.out.println(entity.isStreaming());
            System.out.println(entity.isChunked());
            System.out.println("22222");
            if (entity != null) {
                // 获取字符流
                Reader source = new InputStreamReader(entity.getContent());
                System.out.println(source.ready());
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
