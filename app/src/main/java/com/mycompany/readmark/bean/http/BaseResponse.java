package com.mycompany.readmark.bean.http;

/**
 * Created by Lenovo.
 */

public class BaseResponse {
    private String msg;
    private int code;

    public BaseResponse() {
    }

    public BaseResponse(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
