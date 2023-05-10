package com.npsdk.module.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public final class DeviceUtils {

	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
	private static String uniqueID = null;

	public static String getOSInfo() {
		return Build.MANUFACTURER + " " + Build.PRODUCT + " " + Build.VERSION.RELEASE + " " + Build.VERSION.SDK_INT;
	}

	public static String getDevice() {
		String deviceModel = Build.BRAND + "-" + Build.MODEL;
		return deviceModel;
	}

	public synchronized static String getUniqueID(Context context) {
		if (uniqueID == null) {
			uniqueID = Preference.getString(context, PREF_UNIQUE_ID, null);
			if (uniqueID == null) {
				uniqueID = UUID.randomUUID().toString();
				Preference.save(context, PREF_UNIQUE_ID, uniqueID);
			}
		}
		return uniqueID;
	}

	public static String getDeviceID(Context c) {
		try {
			return getUniqueID(c);
		} catch (Exception e) {
			return null;
		}
	}

	public static String getAndroidID(Context context) {
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}


	public static String getSHACheckSum(String checksum) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(checksum.getBytes());
			byte[] byteData = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}


	public static boolean isAppInstalled(Activity activity, String packageName) {
		PackageManager pm = activity.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			installed = false;
		}
		return installed;
	}

}
