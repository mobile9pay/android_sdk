package com.npsdk.module.model;

import android.content.Context;

import com.npsdk.module.utils.DeviceUtils;

import java.io.Serializable;

public class SdkConfig implements Serializable {

    private final String merchantCode;
    private final String uid;
    private final String env;
    private final int brandColor;

    protected SdkConfig(Builder builder) {
        merchantCode = builder.mMerchantCode;
        uid = builder.mUid;
        env = builder.mEnv;
        brandColor = builder.mBrandColor;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public String getUid() {
        return uid;
    }

    public String getEnv() {
        return env;
    }

    public int getBrandColor() {
        return brandColor;
    }


    public static class Builder {
        private String mMerchantCode;
        private String mUid;
        private String mEnv;
        private int mBrandColor;
        private Context context;
        public Builder(Context context) {
            this.context = context;
        }

        public Builder(String merchantCode, String uid, String env, int brandColor) {
            mMerchantCode = merchantCode;
            mUid = uid;
            mEnv = env;
            mBrandColor = brandColor;
        }

        public Builder merchantCode(String merchantCode) {
            mMerchantCode = merchantCode;
            return this;
        }

        public Builder uid(String uid) {
            if (uid == null || uid.isEmpty()) {
                uid = DeviceUtils.getAndroidID(context);
            }
            mUid = uid;
            return this;
        }

        public Builder env(String env) {
            mEnv = env;
            return this;
        }


        public Builder brandColor(int brandColor) {
            mBrandColor = brandColor;
            return this;
        }


        public SdkConfig build() {
            return new SdkConfig(this);
        }
    }

}

