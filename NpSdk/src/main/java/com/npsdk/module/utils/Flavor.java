package com.npsdk.module.utils;

import android.content.Context;

import com.npsdk.module.NPayLibrary;

public class Flavor {

    public static String baseUrl;
    public static String prefKey;
    public static String baseApi;
    public static String baseShop;

    public void configFlavor(String env) {
        switch (env) {
            case NPayLibrary.STAGING:
                baseUrl = Constants.STAGING_URL;
                prefKey = NPayLibrary.STAGING;
                baseApi = Constants.STAGING_API;
                baseShop = Constants.STAGING_SHOP;
                break;
            case NPayLibrary.SANDBOX:
                baseUrl = Constants.SANDBOX_URL;
                prefKey = NPayLibrary.SANDBOX;
                baseApi = Constants.SANDBOX_API;
                baseShop = Constants.SANDBOX_SHOP;
                break;
            case NPayLibrary.PRODUCTION:
                baseUrl = Constants.PROD_URL;
                prefKey = NPayLibrary.PRODUCTION;
                baseApi = Constants.PROD_API;
                baseShop = Constants.PROD_SHOP;
                break;
        }
    }
}
