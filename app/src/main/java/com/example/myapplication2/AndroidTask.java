package com.example.myapplication2;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.util.Base64;
import java.util.function.Function;

public class AndroidTask {
    private final WebView webView;
    private final Function<JSONObject, String> f;

    private AndroidTask(WebView webView, Function<JSONObject, String> f) {
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
                        JSONObject o = (JSONObject) parser.parse(jsonArgs);
                        result = Base64.encodeToString(f.apply(o).getBytes(), Base64.NO_WRAP);
                        method = "resolve";
                    } catch (Exception e) {
                        result = e.getMessage();
                        method = "reject";
                    }
                    String src = String.format("%s('%s', '%s')", method, cb, result);
                    // do later
                    webView.post(() -> webView.evaluateJavascript(src, null));
                });
        t.start();
    }

    public static void addInvoke(WebView webView, Function<JSONObject, String> f, String name) {
        AndroidTask t = new AndroidTask(webView, f);
        webView.addJavascriptInterface(t, name);
    }
}
