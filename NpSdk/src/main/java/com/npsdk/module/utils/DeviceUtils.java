package com.npsdk.module.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

public final class DeviceUtils {

	public static String getOSInfo() {
		return Build.MANUFACTURER + " " + Build.PRODUCT + " " + Build.VERSION.RELEASE + " " + Build.VERSION.SDK_INT;
	}

	public static String getDevice() {
		String deviceModel = ""+ Build.MODEL;
		deviceModel = removeAccent(deviceModel);
		return deviceModel;
	}

	public static String removeAccent(String s) {
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("");
	}

	private static String uniqueID = null;
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
	public synchronized static String getUniqueID(Context context) {
		if (uniqueID == null) {
			uniqueID = Preference.getString(context, PREF_UNIQUE_ID, null);
			if (uniqueID == null) {
				uniqueID = UUID.randomUUID().toString();
				Preference.save(context , PREF_UNIQUE_ID , uniqueID);
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

	public static String getAndroidID(Context c) {
		return Secure.getString(c.getContentResolver(), Secure.ANDROID_ID);
	}


	public static String getSHACheckSum(String checksum) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1");
			md.update(checksum.getBytes());
			byte byteData[] = md.digest();

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

	public static String stringNormalize(String s){
		try{
//			s = s.toString();
//			s = "á à ă ắ ằ ẵ ẳ â ấ ầ ẫ ẩ ǎ å ǻ ä ǟ ã ȧ ǡ ą ā ả ȁ ȃ ạ ặ ậ ḁ ǽ ǣḃ ḅ ḇ ć ĉ č ċ ç ḉ ď ḋ ḑ ḍ ḓ ḏ é è ĕ ê ế ề ễ ể ě ë ẽ ė ȩ ḝ ę ēḗ ḕ ẻ ȅ ȇ ẹ ệ ḙ ḛ ḟ ǵ ğ ĝ ǧ ġ ģ ḡ ĥ ȟ ḧ ḣ ḩ ḥ ḫ ẖ í ì ĭ î ǐ ï ḯ ĩį ī ỉ ȉ ȋ ị ḭ ĵ ǰ ḱ ǩ ķ ḳ ḵ ĺ ľ ļ ḷ ḹ ḽ ḻ ḿ ṁ ṃ ń ǹ ň ñ ṅ ņ ṇ ṋ ṉ óò ŏ ô ố ồ ỗ ổ ǒ ö ȫ ő õ ṍ ṏ ȭ ȯ ȱ ǫ ǭ ō ṓ ṑ ỏ ȍ ȏ ơ ớ ờ ỡở ợ ọ ộ ǿ ṕ ṗ ŕ ř ṙ ŗ ȑ ȓ ṛ ṝ ṟ ś ṥ ŝ š ṧ ṡ ş ṣ ṩ ș ť ẗ ṫ ţ ṭ ț ṱ ṯ úù ŭ û ǔ ů ü ǘ ǜ ǚ ǖ ű ũ ṹ ų ū ṻ ủ ȕ ȗ ư ứ ừ ữ ử ự ụ ṳ ṷ ṵ ṽṿ ẃ ẁ ŵ ẘ ẅ ẇ ẉ ẍ ẋ ý ỳ ŷ ẙ ÿ ỹ ẏ ȳ ỷ ỵ ź ẑ ž ż ẓ ẕ ǯ ";
//			s = "    upjzxyumilu value: 9999\u0018";
			if(s != null){
				String resultString = Normalizer.normalize(s, Normalizer.Form.NFD);
				resultString = resultString.replaceAll("[^\\x00-\\x7F]", "");
				resultString = resultString.replaceAll("[\u0000-\u001f]", "");
//				Log.d(TAG , "resultString :" + resultString);
				return ""+resultString;
			}else{
				return "";
			}
		}catch (Exception e){
			e.printStackTrace();
			return "";
		}
	}

}
