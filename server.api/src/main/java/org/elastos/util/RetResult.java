package org.elastos.util;

public class RetResult<T> {
    private boolean success;
    private String msg;
    private T data;

    public boolean isSuccess() {
        return success;
    }

    public RetResult<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RetResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public RetResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> RetResult<T> retOk(T data) {
        return new RetResult<T>().setSuccess(true).setData(data);
    }

    public static <T> RetResult<T> retErr(String msg) {
        return new RetResult<T>().setSuccess(true).setMsg(msg);
    }

}
