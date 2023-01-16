

package com.npsdk.module.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfoResponse {

    @SerializedName("server_time")
    @Expose
    private Integer serverTime;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("error_code")
    @Expose
    private Integer errorCode;
    @SerializedName("data")
    @Expose
    private UserInfoModel data;

    /**
     * No args constructor for use in serialization
     */
    public UserInfoResponse() {
    }

    /**
     * @param data
     * @param errorCode
     * @param serverTime
     * @param message
     * @param status
     */
    public UserInfoResponse(Integer serverTime, Integer status, String message, Integer errorCode, UserInfoModel data) {
        super();
        this.serverTime = serverTime;
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.data = data;
    }

    public Integer getServerTime() {
        return serverTime;
    }

    public void setServerTime(Integer serverTime) {
        this.serverTime = serverTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public UserInfoModel getData() {
        return data;
    }

    public void setData(UserInfoModel data) {
        this.data = data;
    }

}