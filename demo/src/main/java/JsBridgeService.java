package com.qianliuAiUi;

import java.util.*;

import browse.JcefBrowserService;
import com.intellij.openapi.project.Project;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.qianliuAiUi.settings.AiReviewSettingsState;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.lang.reflect.Method;


public class JsBridgeService {
    String actionPackageName = "com.qianliuAiUi.jsBridgeActions.";
    static String JsReceiveFuncName = "window.postMessage";
    static String JsPostFuncName = "postMessageToIde";
    Map<String, Object> responseMap ;
    Integer SUCCESS_CODE = 200;
    Integer FAIL_CODE = 500;
    Project currentProject;
    JcefBrowserService jcefBrowserService;

    public JsBridgeService(@NotNull Project project) {
        responseMap = new HashMap<>();
        currentProject = project;
        jcefBrowserService = JcefBrowserService.getInstance(project);
    }
    public String callAction(String args){
//        System.out.println("callAction___" + args);
        String action;
        JSONObject params;
        String cbid = "";

        try{
            JSONObject jsonObject = JSONObject.parseObject(args);
            // 获取action
            action = jsonObject.getString("action");
            // 获取params
            String paramsJsonString = jsonObject.getString("params");
            params = JSONObject.parseObject(paramsJsonString);
            cbid = jsonObject.getString("cbid");

        }catch (Exception e){
            System.out.println(e.getMessage());
            return response(FAIL_CODE,"parse args error: 请检查args格式", cbid);
        }

        try {
            // 按点切割字符串
            String[] splitArray = action.split("\\.");
            if(splitArray.length != 2){
                throw new RuntimeException("args参数异常: " + args);
            }

            String actionClass = Character.toUpperCase(splitArray[0].charAt(0)) + splitArray[0].substring(1) + "Action";
            String actionClassMethod = splitArray[1] ;
            System.out.println("actionClass "+ actionClass + " " + actionClassMethod);
            if (actionClassMethod.equals("notification")){
                params.put("cbid", cbid);
            }

            // 获取目标类的Class对象
            Class<?> targetClass = Class.forName(actionPackageName + actionClass);
            // 创建目标类的实例
            Object targetInstance = targetClass.getDeclaredConstructor(Project.class).newInstance(currentProject);
            // 获取目标方法的Method对象
            Method targetMethod = null;
            try{
                targetMethod = targetClass.getMethod(actionClassMethod, JSONObject.class);
            }catch (Exception e){
                System.out.println("callAction error : actionClassMethod: "+ actionClassMethod
                        + " JSONObject:"
                        + JSONObject.class
                );
                return response(FAIL_CODE, e.getMessage() , cbid);
            }
            // 调用目标方法
            Object result = targetMethod.invoke(targetInstance, params);
            if (result == null){
                return response(SUCCESS_CODE, "", cbid);
            }
            else if (result.equals("notification")) {
                return response(SUCCESS_CODE, "", null);  // 不进行回调
            }
            else{
                // 将结果转换为字符串返回
                return response(SUCCESS_CODE, result, cbid);
            }

        }  catch (Exception e) {
            e.printStackTrace();
            System.out.println("callAction error " + e.getMessage() + " args:" + args);
            // 返回错误响应
            return response(FAIL_CODE, e.getMessage() , cbid);
        }

    }
    public void callActionFromIde(String actionName, Map params){
//        System.out.println("callActionFromIde___"+ actionName + params);
        Map<String, String> args = new HashMap<>();
        params.put("action", actionName);
        args.put("action", actionName);
        args.put("params", JSONObject.toJSONString(params));
        String argsMapStr = JSONObject.toJSONString(args);

        String toolWindowName = AiReviewSettingsState.toolWindowName;
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(currentProject);
        ToolWindow toolWindow = toolWindowManager.getToolWindow(toolWindowName);
        // 浏览器页面加载完成 并且 加载到ide的配置后 则为 ready 的状态
        Boolean browseReady;
        if(jcefBrowserService.getWebBrowseLoadEnd() && jcefBrowserService.getAlreadyGetConfig()){
            browseReady = true;
        } else {
            browseReady = false;
        }

        // 当侧边栏还未展初始化完成， 则直接忽略掉，不在主主动展开侧边栏并初始化webview
        List<String> actionNames = Arrays.asList("editor.selectCode", "editor.saveFile");
        if (toolWindow != null && !browseReady && actionNames.contains(actionName)) {
            System.out.println("toolWindow is not actived actionName: " + actionNames + " is ingored");
            return;
        }

        // 若 toolWindow 未展开，则使其展开
        if (actionName.equals("menu.rightKeyMenu") || actionName.equals("editor.shortkeyReview")) {
            if(toolWindow != null && !toolWindow.isActive()){
                toolWindow.activate(() -> {
                    checkWebBrowseLoadEndCallAction(argsMapStr);
                });
            }
        }

        SwingUtilities.invokeLater(() -> {
            // 如果工具栏中的浏览器已经加载完毕，就不去主动激活展开，直接执行js
            if(browseReady){
                callAction(argsMapStr);
            }else if (toolWindow != null){
                toolWindow.activate(() -> {
                    checkWebBrowseLoadEndCallAction(argsMapStr);
                });
            }

        });

    }
    public void checkWebBrowseLoadEndCallAction(String argsMapStr){
        new Thread(() -> {
            for(int i = 0; i < 10; i++){ // 最多循环10次，每次间隔0.5秒
                System.out.println("start check webBrowseLoadEnd");
                try{
                    Thread.sleep(500);
                }catch(InterruptedException e1){
                    continue;
                }
                // 延迟检查等待浏览器页面加载完成，并且已经获取到ide的配置信息初始化完成
                if(jcefBrowserService.getWebBrowseLoadEnd() && jcefBrowserService.getAlreadyGetConfig()){
                    System.out.println("end check webBrowseLoadEnd");
                    callAction(argsMapStr);
                    return;
                }
            }
        }).start();
    }

    public String response(Integer code, Object content, String cbid ){
        responseMap.put("status_code", code);
        responseMap.put("action", "ideCallback");
        responseMap.put("cbid", cbid);
        if (Objects.equals(code, SUCCESS_CODE)){
            responseMap.put("data", content);
        } else if (Objects.equals(code, FAIL_CODE)) {
            responseMap.put("error_msg", content);
        }

//        System.out.println("responseMap+++++++++++");
//        System.out.println(responseMap);
//        System.out.println(code);
//        System.out.println(content);
//        System.out.println(cbid);
//        System.out.println("responseMap-----------");
        return JSONObject.toJSONString(responseMap);
    }

    public static String getJsReceiveFuncName(){
        return JsReceiveFuncName;
    }
    public static String getJsPostFuncName(){
        return JsPostFuncName;
    }

}
