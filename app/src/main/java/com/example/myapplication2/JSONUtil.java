package com.example.myapplication2;

import org.json.simple.JSONAware;

import java.util.Iterator;
import java.util.List;

public class JSONUtil {
    // could use a more modern json library, but thought this would be fun!
    public static String stringifyList(List<JSONAware> data) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator<JSONAware> iterator = data.iterator();
        JSONAware last = iterator.hasNext() ? iterator.next() : null;
        while(iterator.hasNext()) {
            sb.append(last.toJSONString() + ",");
            last = iterator.next();
        }
        if (last != null) {
            sb.append(last.toJSONString());
        }
        sb.append("]");
        return sb.toString();
    }
}
