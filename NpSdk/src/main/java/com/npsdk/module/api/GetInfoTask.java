package com.npsdk.module.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.npsdk.module.NPayLibrary;
import com.npsdk.module.model.UserInfoResponse;
import com.npsdk.module.utils.Constants;
import com.npsdk.module.utils.Preference;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetInfoTask extends AsyncTask<Void, Void, Void> {
    private final OnGetInfoListener callback;
    private final Context context;
    private String token;

    public GetInfoTask(Context ct, String token, OnGetInfoListener callback) {
        context = ct;
        this.callback = callback;
        this.token = token;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            RestfulApi restfulApi = RestfulApi.getInstance(context);
            Call<UserInfoResponse> call = restfulApi.GetInfoTask(token);
            call.enqueue(new Callback<UserInfoResponse>() {
                @Override
                public void onResponse(@NonNull Call<UserInfoResponse> call, @NonNull Response<UserInfoResponse> response) {
                    Log.d("Response", "response.code() ==   " +response.code());
                    if(response.code() != 200) {
                        callback.onError(response.code(), response.message());
                        return;
                    }
                    UserInfoResponse data = response.body();
                    if (data != null) {
                        if (data.getErrorCode() == 0) {
                            callback.onGetInfoSuccess(data.getData().getBalance().toString(), data.getData().getStatus().toString(), data.getData().getPhone());
                        } else {
                            callback.onError(data.getErrorCode(), data.getMessage());
                        }
                    } else {
                        callback.onError(500, "Có lỗi xảy ra");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UserInfoResponse> callResponse, @NonNull Throwable t) {
                    try {
                        callback.onError(1000, "Có lỗi xảy ra khi kết nối!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            callback.onError(500, "Có lỗi xảy ra");
            e.printStackTrace();
        }
        return null;
    }

    public interface OnGetInfoListener {
        void onGetInfoSuccess(String balance, String status, String phone);

        void onError(int errorCode, String message);
    }
}
