package com.npsdk.module.utils;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.npsdk.module.NPayLibrary;

import org.json.JSONObject;

public class JsHandler {

	private static final int PERMISSION_REQUEST_CODE = 999;
	private static final String TAG = JsHandler.class.getSimpleName();

	private final Activity activity;

	public JsHandler(Activity activity) {
		this.activity = activity;
	}

	@JavascriptInterface
	public void executeFunction(String command, String params) {
		try {
			Log.i(TAG, "executeFunctionP: command = " + command + "; params = " + params);
			JSONObject paramJson = new JSONObject(params);
			switch (switchCommandJS.valueOf(command)) {
				case open9PayApp:
					String appPackageName = "vn.ninepay.ewallet";
					Intent intent = activity.getPackageManager().getLaunchIntentForPackage(appPackageName);
					if (intent == null) {
						// Bring user to the market or let them choose an app?
						intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=" + appPackageName));
					}
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					activity.startActivity(intent);
					break;
				case close:
					NPayLibrary.getInstance().close();
					break;
				case getDeviceID:
					break;
				case share:
					ShareCompat.IntentBuilder.from(activity)
							.setType("text/plain")
							.setChooserTitle("9Pay")
							.setText(paramJson.getString("text"))
							.startChooser();
					break;
				case copy:
					ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
					ClipData clip = ClipData.newPlainText("", paramJson.getString("text"));
					clipboard.setPrimaryClip(clip);
					break;
				case openOtherUrl:
					Intent intentOpenWebView = new Intent();
					intentOpenWebView.setAction("webViewBroadcast");
					intentOpenWebView.putExtra("url", paramJson.getString("url"));
					intentOpenWebView.putExtra("token", paramJson.getString("token"));
					LocalBroadcastManager.getInstance(activity).sendBroadcast(intentOpenWebView);
					break;
				case call:
					Intent callIntent = new Intent(Intent.ACTION_DIAL);
					callIntent.setData(Uri.parse(paramJson.getString("text")));
					activity.startActivity(callIntent);
					break;
				case message:
					Intent messageIntent = new Intent(Intent.ACTION_VIEW);
					messageIntent.setData(Uri.parse(paramJson.getString("text")));
					activity.startActivity(messageIntent);
					break;
				case onLoggedInSuccess:
					NPayLibrary.getInstance().listener.onLoginSuccessful();
					break;
				case onPaymentSuccess:
					NPayLibrary.getInstance().listener.onPaySuccessful();
					break;
				case onError:
					int errorCode = Integer.parseInt(paramJson.getString("error_code"));
					String message = paramJson.getString("message");
					NPayLibrary.getInstance().listener.onError(errorCode, message);
					break;
				case clearToken:
					Preference.remove(activity, NPayLibrary.getInstance().sdkConfig.getEnv() + Constants.ACCESS_TOKEN);
					Preference.remove(activity, NPayLibrary.getInstance().sdkConfig.getEnv() + Constants.REFRESH_TOKEN);
					break;
				case getAllToken:
					Preference.save(activity, NPayLibrary.getInstance().sdkConfig.getEnv() + Constants.ACCESS_TOKEN, paramJson.getString("access_token"));
					Preference.save(activity, NPayLibrary.getInstance().sdkConfig.getEnv() + Constants.REFRESH_TOKEN, paramJson.getString("refresh_token"));
					break;
				case requestCamera:
					_requestCamera(activity);
					break;
				case openSchemaApp:
					openSchemaApp(paramJson.getString("schema"));
					break;
				case logout:
					NPayLibrary.getInstance().logout();
					break;
				default:
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void _requestCamera(Activity activity) {
		ActivityCompat.requestPermissions(activity,
				new String[]{Manifest.permission.CAMERA},
				PERMISSION_REQUEST_CODE);
	}

	void openSchemaApp(String schema) {
		if (schema == null || schema.isEmpty()) return;
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(schema));
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Log.d("OPEN APP", ex.getMessage());
		}
	}

	private enum switchCommandJS {
		open9PayApp,
		close,
		logout,
		openOtherUrl,
		share,
		copy,
		call,
		message,
		clearToken,
		onLoggedInSuccess,
		onPaymentSuccess,
		onError,
		getAllToken,
		getDeviceID,
		requestCamera,
		openSchemaApp,
	}
}