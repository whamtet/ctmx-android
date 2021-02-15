package com.example.myapplication2;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class AndroidBridge {
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final ConcurrentHashMap<String, Function<String, String>> functions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> results = new ConcurrentHashMap<>();
    private final WebView webView;

    public AndroidBridge(WebView webView) {
        this.webView = webView;
        webView.addJavascriptInterface(this, "androidBridge");
    }

    public void addFunction(String fName, Function<String, String> f) {
        functions.put(fName, f);
    }

    @JavascriptInterface
    public String getResult(String cb) {
        String result = results.get(cb);
        results.remove(cb);
        return result;
    }

    @JavascriptInterface
    public void invoke(String fName, String cb, String arg) {
        Function<String, String> f = functions.get(fName);
        executor.execute(() -> {
            String method, result;
            try {
                result = f.apply(arg);
                method = "resolve";
            } catch (Exception e) {
                result = e.getMessage();
                method = "reject";
            }
            results.put(cb, result);
            String src = String.format("%s('%s')", method, cb);
            // do later
            webView.post(() -> webView.evaluateJavascript(src, null));
        });
    }
}
