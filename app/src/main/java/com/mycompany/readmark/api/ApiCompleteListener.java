package com.mycompany.readmark.api;

import com.mycompany.readmark.bean.http.BaseResponse;

/**
 * Created by Lenovo.
 */

public interface ApiCompleteListener {

    void onCompleted(Object result);

    void onFailed(BaseResponse msg);

}
