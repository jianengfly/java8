package com.rograndec.jianeng.annotation.enumtype;

public enum ResponseCode {
    SUCCESS(0, "成功"),
    FAILURE(1, "失败");

    private String message;
    private int code;

    private ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public int getCode() {
        return this.code;
    }

    public String printExceptionMessage() {
        return this.code + ":" + this.message;
    }
}
