package com.hevo;

import com.google.gson.Gson;

public class Utility {
    static Gson gson = new Gson();
    public static <T> T fromJson(Class<T> tClass, String json) {
        return gson.fromJson(json, tClass);
    }
}
