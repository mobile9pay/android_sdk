package com.npsdk;

public interface LibListener {
	
	public static int NOT_LOGIN = 407;
	public static int TOKEN_EXPIRED = 403;
	public static int ERROR_PAYMENT = 10002;

	public void onLoginSuccessful();

	public void onPaySuccessful();

	public void getInfoSuccess(String phone, String balance, String ekycStatus);

	public void onError(int errorCode, String message);

	void getActionMerchantSuccess();
	
	void onLogoutSuccessful();
}
