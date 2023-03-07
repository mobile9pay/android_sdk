package com.npsdk;

public interface LibListener {
	
	public void onLoginSuccessful();

	public void onPaySuccessful();

	public void getInfoSuccess(String phone, String balance, String ekycStatus);

	public void onError(int errorCode, String message);

	public void onLogoutSuccessful();

	public void onCloseSDK();
}
