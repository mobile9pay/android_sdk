package com.npsdk.module.api;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.npsdk.module.model.ActionMerchantResponse;
import com.npsdk.module.model.DataAction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetActionMerchantTask extends AsyncTask<Void, Void, Void> {
    private final OnGetActionListener callback;
    private final Context context;

    public GetActionMerchantTask(Context ct, OnGetActionListener callback) {
        context = ct;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            RestfulApi restfulApi = RestfulApi.getInstance(context);
            Call<ActionMerchantResponse> call = restfulApi.GetActionMerchantTask();
            call.enqueue(new Callback<ActionMerchantResponse>() {
                @Override
                public void onResponse(@NonNull Call<ActionMerchantResponse> call, @NonNull Response<ActionMerchantResponse> response) {
                    if (response.code() != 200) {
                        callback.onError(response.code(), response.message());
                        return;
                    }
                    ActionMerchantResponse data = response.body();
                    if (data != null) {
                        if (data.getErrorCode() == 0) {
                            callback.onGetActionSuccess(data.getData());
                        } else {
                            callback.onError(data.getErrorCode(), data.getMessage());
                        }
                    } else {
                        callback.onError(500, "Có lỗi xảy ra");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ActionMerchantResponse> call, @NonNull Throwable t) {
                    callback.onError(1000, "Có lỗi xảy ra khi kết nối!");

                }
            });
        } catch (Exception e) {
            callback.onError(500, "Có lỗi xảy ra");
            e.printStackTrace();
        }
        return null;
    }

    public interface OnGetActionListener {
        void onGetActionSuccess(List<DataAction> dataActions);

        void onError(int errorCode, String message);
    }
}
