package com.example.myapplication2;
import android.content.Intent;

import java.util.function.Function;

public class ShareImage implements Function<String, String> {
    private final MainActivity activity;
    public ShareImage(MainActivity activity) {
        this.activity = activity;
    }
    @Override
    public String apply(String s) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, s);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        activity.startActivity(shareIntent);

        return "";
    }
}
