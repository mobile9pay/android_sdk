package com.npsdk.module.utils;

import android.util.Log;

public class Utils {
    public static String convertUrlToOrderId(String url) {
        String orderId = last(url.split("/")).trim();
        Log.d("TAG", "orderId: " + orderId);
        return orderId;
    }

    private static <T> T last(T[] array) {
        return array[array.length - 1];
    }
}
