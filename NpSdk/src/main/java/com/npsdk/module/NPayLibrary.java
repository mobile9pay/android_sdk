package com.npsdk.module;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.webkit.WebStorage;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.npsdk.ActionListener;
import com.npsdk.LibListener;
import com.npsdk.module.api.GetActionMerchantTask;
import com.npsdk.module.api.GetInfoTask;
import com.npsdk.module.api.RefreshTokenTask;
import com.npsdk.module.model.DataAction;
import com.npsdk.module.model.SdkConfig;
import com.npsdk.module.utils.Actions;
import com.npsdk.module.utils.Constants;
import com.npsdk.module.utils.DeviceUtils;
import com.npsdk.module.utils.Flavor;
import com.npsdk.module.utils.Preference;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("StaticFieldLeak")
public class NPayLibrary {
    public static final String STAGING = "staging";
    public static final String SANDBOX = "sandbox";
    public static final String PRODUCTION = "prod";
    private static final String TAG = NPayLibrary.class.getSimpleName();
    public static Flavor flavor;
    private static NPayLibrary INSTANCE;
    public SdkConfig sdkConfig;
    public Activity activity;
    public LibListener listener;

    public static NPayLibrary getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NPayLibrary();
            flavor = new Flavor();
        }
        return INSTANCE;
    }

    public void init(Activity activity, SdkConfig sdkConfig, LibListener listener) {
        this.activity = activity;
        this.sdkConfig = sdkConfig;
        this.listener = listener;
        flavor.configFlavor(sdkConfig.getEnv());
    }

    public void openWallet(String actions) {
        Intent intent = new Intent(activity, NPayActivity.class);
        intent.putExtra("data", NPayLibrary.getInstance().walletData(actions));
        activity.startActivity(intent);
    }

    public void login() {
        Intent intent = new Intent(activity, NPayActivity.class);
        intent.putExtra("data", NPayLibrary.getInstance().walletData(Actions.LOGIN));
        activity.startActivity(intent);
    }

    public void pay(String orderId) {
        Intent intent = new Intent(activity, NPayActivity.class);
        intent.putExtra("data", NPayLibrary.getInstance().paymentData(orderId));
        activity.startActivity(intent);
    }

    public void getInfoAccount() {
        if (Preference.getString(activity, Flavor.prefKey + Constants.ACCESS_TOKEN, "").isEmpty()) {
            listener.onError(403, "Chưa login");
            return;
        }
        String token = Preference.getString(activity, Flavor.prefKey + Constants.ACCESS_TOKEN, "");
        String deviceId = DeviceUtils.getDeviceID(activity);
        String UID = DeviceUtils.getUniqueID(activity);
        Log.d(TAG, "device id : " + deviceId + " , UID : " + UID);
        GetInfoTask getInfoTask = new GetInfoTask(activity, "Bearer " + token, new GetInfoTask.OnGetInfoListener() {
            @Override
            public void onGetInfoSuccess(String balance, String status, String phone) {
                listener.getInfoSuccess(phone, status, balance);
            }

            @Override
            public void onError(int errorCode, String message) {
                if (errorCode == 403 || message.contains("đã hết hạn") || message.toLowerCase().contains("không tìm thấy")) {
                    refreshToken(deviceId, UID);
                    return;
                }
                listener.onError(errorCode, message);
            }
        });
        getInfoTask.execute();
    }

    public void getActionMerchant(ActionListener actionListener) {
        String deviceId = DeviceUtils.getDeviceID(activity);
        String UID = DeviceUtils.getUniqueID(activity);
        Log.d(TAG, "device id : " + deviceId + " , UID : " + UID);
        GetActionMerchantTask getActionTask = new GetActionMerchantTask(activity, new GetActionMerchantTask.OnGetActionListener() {
            @Override
            public void onGetActionSuccess(List<DataAction> dataActions) {
                actionListener.getActionMerchantSuccess(dataActions);
            }

            @Override
            public void onError(int errorCode, String message) {
//                if (errorCode == 403) {//TODO a Phương bảo mã lỗi backend chỉ trả về 0 hoặc 1 thôi.
                if (errorCode == 1) {
                    refreshToken(deviceId, UID);
                    return;
                }
                actionListener.onError(errorCode, message);
            }
        });
        getActionTask.execute();
    }


    private void refreshToken(String deviceId, String UID) {
        RefreshTokenTask refreshTokenTask = new RefreshTokenTask(activity, deviceId, UID, new RefreshTokenTask.OnRefreshListener() {
            @Override
            public void onRefreshSuccess() {
                getInfoAccount();
            }

            @Override
            public void onError(int errorCode, String message) {
                listener.onError(errorCode, message);

            }
        }, Preference.getString(activity, NPayLibrary.getInstance().sdkConfig.getEnv() + Constants.REFRESH_TOKEN));
        refreshTokenTask.execute();
    }

    public void logout() {
        WebStorage.getInstance().deleteAllData();
        Preference.remove(activity, NPayLibrary.getInstance().sdkConfig.getEnv() + Constants.ACCESS_TOKEN);
        Preference.remove(activity, NPayLibrary.getInstance().sdkConfig.getEnv() + Constants.REFRESH_TOKEN);
        listener.onLogoutSuccessful();
    }

    public void close() {
        Intent intentClose = new Intent();
        intentClose.setAction("nativeBroadcast");
        intentClose.putExtra("action", "close");
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intentClose);
        listener.onCloseSDK();
    }

    public Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        header.put("Merchant-Code", sdkConfig.getMerchantCode());
        header.put("Merchant-Uid", sdkConfig.getUid());
        header.put("env", sdkConfig.getEnv());
        header.put("App-Type", "SDK");
        return header;
    }

    private String walletData(String route) {
        Map<String, String> data = getHeader();
        data.put("route", route);
        JSONObject obj = new JSONObject(data);
        return obj.toString();
    }

    private String paymentData(String orderId) {
        Map<String, String> data = getHeader();
        data.put("route", "payment_merchant_verify");
        data.put("order_id", orderId);
        JSONObject obj = new JSONObject(data);
        return obj.toString();
    }
}
