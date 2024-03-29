package com.npsdk.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.npsdk.LibListener;
import com.npsdk.module.NPayLibrary;
import com.npsdk.module.model.SdkConfig;
import com.npsdk.module.utils.Actions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	public static final String TAG = "MainActivityLOG";
	TextView userInfo;
	TextView txtMoney;
	WebView webViewGate;
	LinearLayout layoutGate;
	boolean isShow = false;
	Toolbar toolbar;
	EditText edtUrlPaygate;

	@SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		LinearLayout llRutTien = findViewById(R.id.ll_quet_ma);
		LinearLayout llNapTien = findViewById(R.id.ll_nap_tien);
		LinearLayout llChuyenTien = findViewById(R.id.ll_chuyen_tien);
		LinearLayout llTienIch = findViewById(R.id.ll_lich_su);
		LinearLayout llMuaTheDt = findViewById(R.id.ll_thanh_toan_hoa_don);
		LinearLayout llNapTheDT = findViewById(R.id.ll_nap_tien_dt);
		LinearLayout llNapData = findViewById(R.id.ll_mua_the_game);
		LinearLayout ll_mua_the_dich_vu = findViewById(R.id.ll_mua_the_dich_vu);
		LinearLayout ll_mua_the_dt = findViewById(R.id.ll_mua_the_dt);
		LinearLayout ll_nap_data = findViewById(R.id.ll_nap_data);
		LinearLayout layout_sdv = findViewById(R.id.layout_sdv);
		RelativeLayout rlInfo = findViewById(R.id.rl_info);
		ImageView btn_eyes = findViewById(R.id.btn_eyes);
		edtUrlPaygate = findViewById(R.id.edt_url_paygate);
		Button btnThanhToan = findViewById(R.id.btn_thanh_toan);
		View btnClose = findViewById(R.id.btnClose);
		txtMoney = findViewById(R.id.txt_money);
		userInfo = findViewById(R.id.txt_name);
		webViewGate = findViewById(R.id.webView_gate);
		layoutGate = findViewById(R.id.layout_web_gate);
		toolbar = findViewById(R.id.toolbar);
		btnClose = findViewById(R.id.btnClose);

		txtMoney.setTransformationMethod(new AsteriskPasswordTransformationMethod());

		llRutTien.setOnClickListener(this);
		llNapTien.setOnClickListener(this);
		llChuyenTien.setOnClickListener(this);
		llTienIch.setOnClickListener(this);
		llMuaTheDt.setOnClickListener(this);
		llNapTheDT.setOnClickListener(this);
		llNapData.setOnClickListener(this);
		ll_mua_the_dich_vu.setOnClickListener(this);
		ll_mua_the_dt.setOnClickListener(this);
		ll_nap_data.setOnClickListener(this);
		layout_sdv.setOnClickListener(this);
		btn_eyes.setOnClickListener(this);
		rlInfo.setOnClickListener(this);
		btnClose.setOnClickListener(this);
		btnThanhToan.setOnClickListener(this);

		SdkConfig sdkConfig = new SdkConfig.Builder(this).merchantCode("sdk_test").uid(null).env(NPayLibrary.STAGING).brandColor(0xff15AE62).build();
		initSdk(sdkConfig);

	}


	private void initSdk(SdkConfig sdkConfig) {
		NPayLibrary.getInstance().init(MainActivity.this, sdkConfig, new LibListener() {
			@Override
			public void onLoginSuccessful() {

			}

			@Override
			public void onPaySuccessful() {

			}

			@Override
			public void getInfoSuccess(String phone, String balance, String ekycStatus) {
				userInfo.setText("Hi," + phone);
				txtMoney.setText(balance + "đ");
			}

			@Override
			public void onError(int errorCode, String message) {

			}

			@Override
			public void onLogoutSuccessful() {
				System.out.println("Logout success!");
			}

			@Override
			public void onCloseSDK() {

			}
		});

	}

	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_quet_ma:
				Log.d(TAG, "onClick: ll_rut_tien");
				NPayLibrary.getInstance().openWallet(Actions.QR);
				break;
			case R.id.ll_nap_tien:
				Log.d(TAG, "onClick: ll_nap_tien");
				NPayLibrary.getInstance().openWallet(Actions.DEPOSIT);
				break;
			case R.id.ll_chuyen_tien:
				NPayLibrary.getInstance().openWallet(Actions.TRANSFER);
				Log.d(TAG, "onClick: ll_chuyen_tien");
				break;
			case R.id.ll_lich_su:
				Log.d(TAG, "onClick: ll_lich_su");
				NPayLibrary.getInstance().openWallet(Actions.HISTORY);
				break;
			case R.id.ll_thanh_toan_hoa_don:
				Log.d(TAG, "onClick: ll_thanh_toan_hoa_don");
				NPayLibrary.getInstance().openWallet(Actions.SHOP);

				break;
			case R.id.ll_nap_tien_dt:
				Log.d(TAG, "onClick: ll_nap_tien_dt");
				NPayLibrary.getInstance().openWallet(Actions.TOPUP_PHONE_CARD);
				break;
			case R.id.ll_mua_the_game:
				Log.d(TAG, "onClick: ll_mua_the_game");
				NPayLibrary.getInstance().openWallet(Actions.MG_CARD);
				break;
			case R.id.ll_mua_the_dich_vu:
				Log.d(TAG, "onClick: ll_mua_the_dich_vu");
				NPayLibrary.getInstance().openWallet(Actions.BUY_SERVICES_CARD);
				break;
			case R.id.ll_mua_the_dt:
				Log.d(TAG, "onClick: ll_mua_the_dt");
				NPayLibrary.getInstance().openWallet(Actions.BUY_PHONE_CARD);
				break;
			case R.id.ll_nap_data:
				Log.d(TAG, "onClick: ll_nap_data");
				NPayLibrary.getInstance().openWallet(Actions.TOPUP_DATA_CARD);
				break;
			case R.id.rl_info:
				Log.d(TAG, "onClick: rl_info");
				break;
			case R.id.btn_eyes:
				Log.d(TAG, "onClick: btn_eyes");
				if (isShow) {
					txtMoney.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
				} else {
					txtMoney.setTransformationMethod(new AsteriskPasswordTransformationMethod());
				}
				isShow = !isShow;
				break;
			case R.id.layout_sdv:
				Log.d(TAG, "onClick: layout_sdv");
				NPayLibrary.getInstance().openWallet(Actions.OPEN_WALLET);
				break;
			case R.id.btnClose:
				layoutGate.setVisibility(View.GONE);
				break;
			case R.id.btn_thanh_toan:
				//paste url thanh toán vào hàm pay
				String test = "";
				NPayLibrary.getInstance().pay(test);
				break;

		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}

class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
	@Override
	public CharSequence getTransformation(CharSequence source, View view) {
		return new PasswordCharSequence(source);
	}

	private class PasswordCharSequence implements CharSequence {
		private final CharSequence mSource;

		public PasswordCharSequence(CharSequence source) {
			mSource = source; // Store char sequence
		}

		public char charAt(int index) {
			return '*'; // This is the important part
		}

		public int length() {
			return mSource.length(); // Return default
		}

		public CharSequence subSequence(int start, int end) {
			return mSource.subSequence(start, end); // Return default
		}
	}
}