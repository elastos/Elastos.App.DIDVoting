package org.elastos.util;

public class RetResultB<T> {
    private boolean success;
    private String msg;
    private T data;

    public boolean isSuccess() {
        return success;
    }

    public RetResultB<T> setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RetResultB<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public RetResultB<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static <T> RetResultB<T> retOk(T data) {
        return new RetResultB<T>().setSuccess(true).setData(data);
    }

    public static <T> RetResultB<T> retErr(String msg) {
        return new RetResultB<T>().setSuccess(false).setMsg(msg);
    }

}
