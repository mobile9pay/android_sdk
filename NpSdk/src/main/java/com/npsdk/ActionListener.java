package com.npsdk;

import com.npsdk.module.model.DataAction;

import java.util.List;

public interface ActionListener {
    public void onError(int errorCode, String message);

    public void getActionMerchantSuccess(List<DataAction> dataActions);

}