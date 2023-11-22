package com.common;

import com.alibaba.fastjson2.JSONObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;
import com.common.CacheUtil;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class util {
    public static String ideLanguageId = "textmate";

    // 获取文件后缀，根据文件名
    private static String getExtensionByFilename(String filename) {
        if (!filename.contains(".")) {
            return "";
        } else if (filename.matches("^\\.[0-9a-zA-Z_]+$")) {
            return filename;
        }

        String[] parts = filename.split("\\.");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        } else {
            return "";
        }
    }

    // 获取语言，根据文件名
    public static String getLanguageByFilename(String filename) {
        String fileExtension = getExtensionByFilename(filename).toLowerCase();
        List<Map<String, Object>> languageExtensions = CacheUtil.languageExtensions;
        for (Map<String, Object> item : languageExtensions) {
            ArrayList fileExtensions = (ArrayList) item.get("file_extensions");
            if (fileExtensions.contains(fileExtension)) {
                String language = (String) item.get("language");
                return language.toLowerCase();
            }
        }

        return util.ideLanguageId;
    }
    public static void setIdeLanguageId(String ideLanguageId) {
        util.ideLanguageId = ideLanguageId;
    }

    // 获取语言，根据文件路径
    public static String getLanguageByFilePath(String filePath) {
        Path path = Paths.get(filePath);
        File file = new File(path.toUri());
        String filename = file.getName();
        return getLanguageByFilename(filename);
    }

    public static Project getCurrentProjectFromDocument(Document document){
        FileDocumentManager fileDocumentManager = FileDocumentManager.getInstance();
        VirtualFile virtualFile = fileDocumentManager.getFile(document);
        Project project = null;
        if (virtualFile != null) {
            // 获取当前的project
            project = ProjectUtil.guessProjectForFile(virtualFile);
        }
        return project;
    }

    public static Document getSelectedDocument(Project project){
        Document selectDocument = ApplicationManager.getApplication().runReadAction((ThrowableComputable<Document, RuntimeException>) () -> {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            VirtualFile @NotNull [] activeVirtualFile = fileEditorManager.getSelectedFiles();
            if (activeVirtualFile.length == 0){
                return null;
            }
            Document document = FileDocumentManager.getInstance().getDocument(activeVirtualFile[0]);
            return document;

        });

        return selectDocument;
    }

    public static Map<String, Object> getIdeThemeColor(JSONObject params){
        Map<String, String> colorMap = new HashMap<>();
        colorMap.put("textColor", "TextField.foreground");
        colorMap.put("editorBgColor", "EditorPane.background");
        colorMap.put("buttonColor", "Button.foreground");
        colorMap.put("buttonBgColor", "Button.background");
        colorMap.put("inputColor", "TextField.foreground");
        colorMap.put("inputBgColor", "TextField.background");
        colorMap.put("dropdownColor", "List.foreground");
        colorMap.put("dropdownBgColor", "List.background");
        colorMap.put("borderColor", "Component.borderColor");
        colorMap.put("scrollColor", "ScrollBar.thumb");
        colorMap.put("scrollBgColor", "ScrollBar.background");
        // 循环取键值
        for (Map.Entry<String, String> entry : colorMap.entrySet()) {
            try{
                String key = entry.getKey();
                String value = entry.getValue();
                params.put(key, UIManager.getColor(value).toString());
            }catch (Exception e){
                System.out.println("cannt find UIManager key:" + entry.getKey());
                continue;
            }

        }
        return params;
    }

    public static Map<String, Object> getIdeThemeStyle(JSONObject params){
        System.out.println(UIManager.getLookAndFeel().getName());
        // setting 里面设置主题的时候 包含Darcula 均是 暗色系 其它 则是亮色系
        String themeKind = UIManager.getLookAndFeel().getName().contains("Darcula") ? "vs-dark" : "vs";
        params.put("themeKind", themeKind);
        return params;
    }

    public static String getFileExtensionByLanguage(String language) {
        Map<String, String> languageExtensions = new HashMap<>();
        languageExtensions.put("java", ".java");
        languageExtensions.put("python", ".py");
        languageExtensions.put("c", ".c");
        languageExtensions.put("c++", ".cpp");
        languageExtensions.put("c#", ".cs");
        languageExtensions.put("javascript", ".js");
        languageExtensions.put("ruby", ".rb");
        languageExtensions.put("go", ".go");
        languageExtensions.put("swift", ".swift");
        languageExtensions.put("php", ".php");
        languageExtensions.put("rust", ".rs");
        languageExtensions.put("typescript", ".ts");
        languageExtensions.put("kotlin", ".kt");
        languageExtensions.put("scala", ".scala");
        languageExtensions.put("perl", ".pl");
        languageExtensions.put("r", ".r");
        languageExtensions.put("matlab", ".m");
        languageExtensions.put("objective-c", ".m");
        languageExtensions.put("shell", ".sh");
        languageExtensions.put("html", ".html");
        languageExtensions.put("json", ".json");
        languageExtensions.put("yaml", ".yaml");
        languageExtensions.put("swagger", ".yaml");
        if(language == null){
            return ".txt";
        }
        return languageExtensions.getOrDefault(language.toLowerCase(), ".txt");

    }

    public static String getGitPathByFilePath(Project project){
        try {
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            VirtualFile virtualFile = fileEditorManager.getSelectedFiles()[0];
            String filePath = virtualFile.getPath();
            File file = new File(filePath);
            // 获取当前执行划词对话的文件 所在的目录
            String directoryPath = file.getParent();
            ProcessBuilder builder = new ProcessBuilder("git", "remote", "get-url", "origin");
            builder.directory(new File(directoryPath));
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String gitPath = reader.readLine();
            return gitPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}


