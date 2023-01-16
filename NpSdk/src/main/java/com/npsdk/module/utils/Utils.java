package com.npsdk.module.utils;

import android.util.Log;

public class Utils {
    public static String convertUrlToOrderId(String url) {
        String orderId = url.replaceAll(Constants.URL_PORTAL_STG, "")
                .replaceAll(Constants.URL_PORTAL_SAND, "")
                .replaceAll(Constants.URL_PORTAL_PROD, "");
        Log.d("TAG", "orderId: " + orderId);
        return orderId;
    }
}
