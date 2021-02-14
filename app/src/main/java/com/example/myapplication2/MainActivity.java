package com.example.myapplication2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.simple.JSONObject;

import java.util.Optional;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        (new ImapTask()).start();

        WebView myWebView = new WebView(this.getApplicationContext());
        myWebView.getSettings().setJavaScriptEnabled(true);

        // add implementation "androidx.webkit:webkit:1.2.0" to build.gradle
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            @RequiresApi(21)
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        setContentView(myWebView);
        Function<Optional<JSONObject>, String> f = o -> o.map(o2 -> o2.toJSONString()).orElse("");
        AndroidTask.addInvoke(myWebView, f, "invoke");
        myWebView.loadUrl("https://appassets.androidplatform.net/assets/www/index.html");
    }

}