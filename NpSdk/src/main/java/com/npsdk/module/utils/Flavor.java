package com.npsdk.module.utils;

import com.npsdk.module.NPayLibrary;

public class Flavor {

    public static String baseUrl;
    public static String prefKey;
    public static String baseApi;

    public void configFlavor(String env) {
        switch (env) {
            case NPayLibrary.STAGING:
                baseUrl = Constants.STAGING_URL;
                prefKey = NPayLibrary.STAGING;
                baseApi = Constants.STAGING_API;
                break;
            case NPayLibrary.SANDBOX:
                baseUrl = Constants.SANDBOX_URL;
                prefKey = NPayLibrary.SANDBOX;
                baseApi = Constants.SANDBOX_API;
                break;
            case NPayLibrary.PRODUCTION:
                baseUrl = Constants.PROD_URL;
                prefKey = NPayLibrary.PRODUCTION;
                baseApi = Constants.PROD_API;
                break;
        }
    }

}
