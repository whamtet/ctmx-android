package com.example.myapplication2;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Optional;
import java.util.function.Function;

public class AndroidTask {
    private final WebView webView;
    private final Function<Optional<JSONObject>, String> f;

    private AndroidTask(WebView webView, Function<Optional<JSONObject>, String> f) {
        this.webView = webView;
        this.f = f;
    }

    @JavascriptInterface
    public void invoke(String cb, String jsonArgs) {
        Thread t = new Thread(
                () -> {
                    String method, result;
                    JSONParser parser = new JSONParser();
                    try {
                        Optional<JSONObject> o = jsonArgs == null || jsonArgs.length() == 0 ?
                                Optional.empty() :
                                Optional.of((JSONObject) parser.parse(jsonArgs));
                        result = "'" + f.apply(o) + "'";
                        method = "resolve";
                    } catch (Exception e) {
                        result = e.getMessage();
                        method = "reject";
                    }
                    String src = String.format("%s('%s', %s)", method, cb, result);
                    // do later
                    webView.post(() -> webView.evaluateJavascript(src, null));
                });
        t.start();
    }

    public static void addInvoke(WebView webView, Function<Optional<JSONObject>, String> f, String name) {
        AndroidTask t = new AndroidTask(webView, f);
        webView.addJavascriptInterface(t, name);
    }
}
