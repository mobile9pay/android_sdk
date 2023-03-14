package com.npsdk.module;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.npsdk.module.utils.Actions;
import com.npsdk.module.utils.Flavor;
import com.npsdk.module.utils.JsHandler;
import com.npsdk.module.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NPayActivity extends AppCompatActivity {
    public static final String TAG = NPayActivity.class.getName();
    Map<String, String> headerWebView = NPayLibrary.getInstance().getHeader();
    public static WebView webView, webView2;
    private View btnClose;
    private Toolbar toolbar;
    private BroadcastReceiver changeUrlBR;
    private RelativeLayout rlOverlay;
    private JsHandler jsHandler;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npay);
        findView();
        closeButtonWebview();
        jsHandler = new JsHandler(this);
        String data = getIntent().getStringExtra("data");
        Log.d(TAG, "onCreate: data ==   " + data);

        IntentFilter filter = new IntentFilter();
        filter.addAction("webViewBroadcast");
        filter.addAction("nativeBroadcast");
        listentChangeUrlBroadcast();

        LocalBroadcastManager.getInstance(this).registerReceiver(changeUrlBR, filter);

        settingWebview(webView);
        settingWebview(webView2);

        setUpweb1Client();
        setUpWeb2Client(data);
        try {
            Uri.Builder builder = new Uri.Builder();
            JSONObject jsonObject = new JSONObject(data);
            String route = jsonObject.getString("route");

            if (route.equals("payment_merchant_verify")) {
                String orderId = jsonObject.getString("order_id");
                if (orderId.isEmpty()) {
                    Toast.makeText(NPayActivity.this, "Sai định dạng url", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                webView.setVisibility(View.GONE);
                webView2.clearCache(true);
                webView2.clearHistory();
                webView2.setVisibility(View.VISIBLE);
                rlOverlay.setVisibility(View.VISIBLE);
                webView2.loadUrl(orderId, headerWebView);
                showOrHideToolbar();
                //tạm thời dùng delay 30s để callback payment faield
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (webView2 != null && !webView2.getUrl().contains("/merchant/payment/")) {
                            //thông báo cho merchant thông báo có lỗi khi k thể load đc url payment
                            NPayLibrary.getInstance().listener.onError(249, "Sai định dạng url thanh toán.");
                            finish();
                        }
                        handler.removeCallbacksAndMessages(null);
                    }
                }, 30 * 1000);
                return;
            }

            if (route.equals(Actions.SHOP)) {
                webView.setVisibility(View.GONE);
                webView2.setVisibility(View.VISIBLE);
                webView2.loadUrl(Flavor.baseShop + "/hoa-don-thanh-toan/", headerWebView);
                showOrHideToolbar();
            } else {
                builder.scheme("https")
                        .authority(/*"10.1.20.37:8080"*/ Flavor.baseUrl.replaceAll("https://", ""))
                        .appendPath("direct")
                        .appendQueryParameter("route", jsonObject.getString("route"))
                        .appendQueryParameter("Merchant-Code", jsonObject.getString("Merchant-Code"))
                        .appendQueryParameter("Merchant-Uid", jsonObject.getString("Merchant-Uid"))
                        .appendQueryParameter("App-version-Code", "375")
                        .appendQueryParameter("brand_color", String.valueOf(NPayLibrary.getInstance().sdkConfig.getBrandColor()))
                        .appendQueryParameter("platform", "android");
                if (jsonObject.has("order_id")) {
                    builder.appendQueryParameter("order_id", Utils.convertUrlToOrderId(jsonObject.getString("order_id")));
                }
                Log.d(TAG, "onCreate: Flavor.baseUrl ==   " + builder);
                webView2.clearCache(true);
                webView2.clearHistory();
                webView.loadUrl(builder.toString(), headerWebView);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void setUpWeb2Client(String data) {
        webView2.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d(TAG, "shouldOverrideUrlLoading 2: url ==   " + url);

                if (url.endsWith("close-webview")) {
                    clearWeb2();
                    return false;
                }

                //dành cho thanh toán merchant, bóc tách orderID
                if (url.contains("/merchant/payment/") || url.contains("/thanh-toan-qr/")) {
                    try {
                        Uri.Builder builder = new Uri.Builder();
                        JSONObject jsonObject = new JSONObject(data);

                        builder.scheme("https")
                                .authority(/*"10.1.20.37:8080"*/ Flavor.baseUrl.replaceAll("https://", ""))
                                .appendPath("direct")
                                .appendQueryParameter("route", jsonObject.getString("route"))
                                .appendQueryParameter("Merchant-Code", jsonObject.getString("Merchant-Code"))
                                .appendQueryParameter("Merchant-Uid", jsonObject.getString("Merchant-Uid"))
                                .appendQueryParameter("App-version-Code", "375")
                                .appendQueryParameter("brand_color", String.valueOf(NPayLibrary.getInstance().sdkConfig.getBrandColor()))
                                .appendQueryParameter("platform", "android")
                                .appendQueryParameter("order_id", Utils.convertUrlToOrderId(url));
                        webView2.clearCache(true);
                        webView2.clearHistory();
                        webView2.setVisibility(View.GONE);
                        rlOverlay.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        webView.loadUrl(builder.toString(), headerWebView);

                    } catch (Exception e) {
                    }
                    return false;

                }

                if (url.startsWith(Flavor.baseUrl) && !url.contains("kyc")) {
                    clearWeb2();
                    webView.loadUrl(url, headerWebView);
                    return false;
                }
                webView2.loadUrl(url, headerWebView);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //TODO xử lý load lỗi
                Log.d(TAG, "onPageFinished: " + url);
            }
        });
    }

    private void setUpweb1Client() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.contains(Flavor.baseUrl)) {
                    webView.setVisibility(View.GONE);
                    webView2.clearCache(true);
                    webView2.clearHistory();
                    webView2.setVisibility(View.VISIBLE);
                    webView2.loadUrl(url, headerWebView);
                    return false;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showOrHideToolbar();
                super.onPageStarted(view, url, favicon);
            }

        });
    }

    private void listentChangeUrlBroadcast() {
        changeUrlBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "onReceive:  ==   " + intent.getAction());
                if (intent.getAction().equals("webViewBroadcast")) {
                    if (webView2 != null) {
                        webView2.clearCache(true);
                        webView2.clearHistory();
                    }
                    String getURL = intent.getStringExtra("url");
                    if (getURL.startsWith(Flavor.baseUrl + "/v1/kyc")) {
                        Map<String, String> extraHeaders = new HashMap<>();
                        String token = intent.getStringExtra("token");
                        if (token != null) extraHeaders.put("Authorization", token);
                        extraHeaders.putAll(NPayLibrary.getInstance().getHeader());
                        webView.setVisibility(View.GONE);
                        webView2.setVisibility(View.VISIBLE);
                        webView2.loadUrl(getURL, extraHeaders);
                        showOrHideToolbar();
                        return;
                    }
                    if (!getURL.startsWith(Flavor.baseUrl)) {
                        webView.setVisibility(View.GONE);
                        webView2.setVisibility(View.VISIBLE);
                        webView2.loadUrl(getURL, headerWebView);
                    }
                    showOrHideToolbar();

                }
                if (intent.getAction().equals("nativeBroadcast")) {
                    switch (intent.getStringExtra("action")) {
                        case "close":
                            finish();
                            break;

                    }
                }
            }
        };
    }

    private void findView() {
        webView = findViewById(R.id.webView);
        webView2 = findViewById(R.id.webView2);
        toolbar = findViewById(R.id.toolbar);
        btnClose = findViewById(R.id.btnClose);
        rlOverlay = findViewById(R.id.rl_overlay);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void settingWebview(WebView webView) {
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.addJavascriptInterface(jsHandler, "JsHandler");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setPluginState(WebSettings.PluginState.ON);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(changeUrlBR);
        webView.clearHistory();
        webView.clearCache(true);
        webView.destroy();
        webView2.clearCache(true);
        webView2.destroy();
        closeCamera();
        super.onDestroy();
    }

    public void clearWeb2() {
        if (webView2.getVisibility() == View.VISIBLE) {
            webView2.clearHistory();
            webView2.clearCache(true);
            webView2.clearFormData();
            webView2.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
        showOrHideToolbar();
    }


    void closeButtonWebview() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView2.getUrl().contains("v1/kyc/ket-qua")) {
                    webView.loadUrl(Flavor.baseUrl + "#home?reload=true");
                }
                clearWeb2();
            }
        });
    }

    void showOrHideToolbar() {
        if (webView2.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.VISIBLE);
            return;
        }
        if (toolbar.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.GONE);
            closeCamera();
        }

        if (webView.getUrl() == null) {
            finish();
        }
    }

    void closeCamera() {
        try {
            Camera.open().release();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == JsHandler.PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                JsHandler.sendStatusCamera(true);
            } else {
                JsHandler.sendStatusCamera(false);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}