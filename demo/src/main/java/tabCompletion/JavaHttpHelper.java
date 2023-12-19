package tabCompletion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.extensions.PluginId;
import tabCompletion.vo.RequestVO;
import tabCompletion.vo.ResponseVO;
import okhttp3.*;
import settings.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JavaHttpHelper {

//    List<HttpURLConnection> connections = new ArrayList<>();

    private String ideName = ApplicationInfo.getInstance().getVersionName();
    private String ideVersion = PluginManager.getPlugin(PluginId.getId(Constants.PLUGIN_ID)).getVersion();
    private String ideRealVersion = ApplicationInfo.getInstance().getFullVersion() + "  " +
            ApplicationInfo.getInstance().getApiVersion();

    public String getIdeName() {
        return ideName;
    }

    public String getIdeVersion() {
        return ideVersion;
    }

    public String getIdeRealVersion() {
        return ideRealVersion;
    }

    public static ResponseVO post(String url, RequestVO requestVO, String apiKey,String completionAdjustment_hash_code) throws IOException {
        // create URL obj
        URL requestUrl = new URL(url);
        // open connection
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        // save current connection with completionAdjustment_hash_code
//        InlineCompletionHandler.connection_dict.put(completionAdjustment_hash_code, connection);
        connection.setReadTimeout(50000);
        connection.setConnectTimeout(50000);
        System.out.println("------------requestVO-----------");
//        String last_completionAdjustment_hash_code = Integer.toHexString(InlineCompletionHandler.last_completionAdjustment.hashCode());

        // set POST request method
        connection.setRequestMethod("POST");

        // set request header
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        JavaHttpHelper http = new JavaHttpHelper();
        connection.setRequestProperty("ide", http.getIdeName());
        connection.setRequestProperty("ide-version", http.getIdeVersion());
        connection.setRequestProperty("ide-real-version", http.getIdeRealVersion());
        connection.setRequestProperty("ide-real-version", http.getIdeRealVersion());
        connection.setRequestProperty("model", "gemini-pro");
        connection.setRequestProperty("service", "gemini");

        // enable output
        connection.setDoOutput(true);

        Gson gson = new Gson();
        // create request body
        // write outputStream
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            byte[] requestBodyBytes = gson.toJson(requestVO).getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBodyBytes, 0, requestBodyBytes.length);
        }

        // send request
        connection.connect();

//        if (InlineCompletionHandler.connection_dict.size() > 0) {
//            for (Map.Entry<String, HttpURLConnection> entry : InlineCompletionHandler.connection_dict.entrySet()) {
//                String key = entry.getKey();
//                HttpURLConnection value = entry.getValue();
//                if (!key.equals(last_completionAdjustment_hash_code)){
//                    value.disconnect();
//                }
//            }
//        }

        // read response
//        int responseCode = connection.getResponseCode();
//        String responseMessage = connection.getResponseMessage();

        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseBody.append(line);
            }
        } finally {
            connection.disconnect();
//            InlineCompletionHandler.connection_dict.remove(completionAdjustment_hash_code);
        }
        JsonObject emptyJsonObject = new JsonObject();
        String emptyJsonString = gson.toJson(emptyJsonObject);

        if (responseBody.length() > 0) {
            try{
                return gson.fromJson(responseBody.toString(), ResponseVO.class);
            }catch (Exception e){
                System.out.println("gson.fromJson error: " + responseBody.toString());
                return gson.fromJson(emptyJsonString, ResponseVO.class);
            }
        }else{
            return gson.fromJson(emptyJsonString, ResponseVO.class);
        }

    }

    public static ResponseVO postByOkHttp3(String url, RequestVO requestVO, String apiKey) throws IOException {
        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(gson.toJson(requestVO), MediaType.get("application/json"));

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        Response response = client.newCall(request).execute();
        response = response.newBuilder()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(ResponseBody.create(response.body().contentType(), response.body().string())).build();
//        response = responseBuilder.build();
        return gson.fromJson(response.body().string(), ResponseVO.class);
    }
    public static int postCodeCompletionUpload(String url, RequestVO requestVO, String apiKey) throws IOException {
        // 发起自动补全数据上报请求
        Gson gson = new Gson();

        RequestBody requestBody = RequestBody.create(gson.toJson(requestVO), MediaType.get("application/json"));

        System.out.println("code_completion_log:" + url);
        JavaHttpHelper http = new JavaHttpHelper();
        String user_token = apiKey;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("api-key", user_token)
                .addHeader("ide", http.getIdeName())
                .addHeader("ide-version", http.getIdeVersion())
                .addHeader("ide-real-version", http.getIdeRealVersion())
                .build();
        Response response = client.newCall(request).execute();
        response = response.newBuilder()
                .header("Content-Type", "application/json; charset=utf-8")
                .body(ResponseBody.create(response.body().contentType(), response.body().string())).build();

        return response.code();
    }

}
