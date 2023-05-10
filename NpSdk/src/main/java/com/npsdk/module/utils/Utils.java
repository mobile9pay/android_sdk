package com.npsdk.module.utils;

import android.app.Activity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class Utils {
    public static String convertUrlToOrderId(String url) {
        String orderId = last(url.split("/")).trim();
        Log.d("TAG", "orderId: " + orderId);
        return orderId;
    }

    private static <T> T last(T[] array) {
        return array[array.length - 1];
    }

    public static String getUrlActionShop(String action) {
        String path = null;
        switch (action) {
            case Actions.SHOP:
                return Flavor.baseShop + "/hoa-don-thanh-toan/";
            case Actions.BILLING_DIEN:
                path = "hoa-don-tien-dien";
                break;
            case Actions.BILLING_TRUYEN_HINH:
                path = "hoa-don-truyen-hinh";
                break;
            case Actions.BILLING_DIEN_THOAI:
                path = "hoa-don-dien-thoai-co-dinh";
                break;
            case Actions.BILLING_INTERNET:
                path = "hoa-don-internet";
                break;
            case Actions.BILLING_NUOC:
                path = "hoa-don-nuoc";
                break;
            case Actions.BILLING_BAO_HIEM:
                path = "hoa-don-bao-hiem";
                break;
            case Actions.BILLING_TAI_CHINH:
                path = "hoa-don-tai-chinh";
                break;
            case Actions.BILLING_TRA_SAU:
                path = "hoa-don-tra-sau";
                break;
            case Actions.BILLING_TIN_DUNG:
                path = "hoa-don-the-tin-dung";
                break;
            case Actions.BILLING_HOC_PHI:
                path = "hoa-don-hoc-phi";
                break;
            case Actions.BILLING_TRA_GOP:
                path = "hoa-don-tra-gop";
                break;
            case Actions.BILLING_VE_TAU_XE:
                path = "hoa-don-ve-xe";
                break;
            case Actions.BILLING_VETC:
                path = "hoa-don-duong-bo";
                break;
        }

        if (path == null) {
            return Flavor.baseShop + "/hoa-don-thanh-toan/";
        }
        return Flavor.baseShop + "/hoa-don/" + path;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }
}
