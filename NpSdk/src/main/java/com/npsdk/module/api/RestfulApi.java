package com.npsdk.module.api;

import android.content.Context;

import com.npsdk.module.NPayLibrary;
import com.npsdk.module.model.ActionMerchantResponse;
import com.npsdk.module.model.RefreshTokenResponse;
import com.npsdk.module.model.UserInfoResponse;
import com.npsdk.module.utils.Constants;
import com.npsdk.module.utils.Flavor;
import com.npsdk.module.utils.Preference;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public class RestfulApi {
    private PlfRestService restService;
    private static Context mContext;

    public static RestfulApi getInstance(Context context) {
        mContext = context;
        return new RestfulApi();
    }

    private RestfulApi() {
        try {
            OkHttpClient httpClient = getCommonClient();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Flavor.baseApi)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();
            restService = retrofit.create(PlfRestService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OkHttpClient getCommonClient() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = null;
        try {
            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request.Builder builder = chain.request()
                                .newBuilder()
                                .addHeader("Merchant-Code", "sdk_test").addHeader("App-version-Code", "375");

                        Request request = builder.build();
                        return chain.proceed(request);
                    })
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return httpClient;
    }

    public Call<UserInfoResponse> GetInfoTask(String token) {
        return restService.getInfoTask(token);
    }

    public Call<RefreshTokenResponse> RefreshTokenTask(String refreshToken, String deviceId, String UID) {
        return restService.refreshToken(refreshToken, deviceId, UID);
    }

    public Call<ActionMerchantResponse> GetActionMerchantTask() {
        return restService.getActionMerchantTask();
    }

    public interface PlfRestService {

        @GET("/sdk/v2/user/info")
        Call<UserInfoResponse> getInfoTask(@Header("Authorization") String token);

        @FormUrlEncoded
        @POST("/sdk/v1/login/refresh_token")
        Call<RefreshTokenResponse> refreshToken(
                @Field("refresh_token") String refreshToken,
                @Header("Device-ID") String deviceId,
                @Header("Unique-ID") String UID
        );

        @GET("/sdk/v1/func/list")
        Call<ActionMerchantResponse> getActionMerchantTask();

    }

}
