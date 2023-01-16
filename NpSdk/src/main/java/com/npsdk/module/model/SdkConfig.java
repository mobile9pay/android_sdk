package com.npsdk.module.model;

import java.io.Serializable;

public class SdkConfig implements Serializable {
    private final String merchantCode;
    private final String uid;
    private final String env;
    private final int brandColor;
    private final String localUrl;

    protected SdkConfig(Builder builder) {
        merchantCode = builder.mMerchantCode;
        uid = builder.mUid;
        env = builder.mEnv;
        brandColor = builder.mBrandColor;
        localUrl = builder.mLocalUrl;
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
        private String mLocalUrl;

        public Builder() {
        }

        public Builder(int brandColor, String merchantCode, String uid, String env, String localUrl) {
            mMerchantCode = merchantCode;
            mUid = uid;
            mEnv = env;
            mBrandColor = brandColor;
            mLocalUrl = localUrl;
        }

        public Builder localUrl(String localUrl) {
            mLocalUrl = localUrl;
            return this;
        }


        public Builder merchantCode(String merchantCode) {
            mMerchantCode = merchantCode;
            return this;
        }

        public Builder uid(String uid) {
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

