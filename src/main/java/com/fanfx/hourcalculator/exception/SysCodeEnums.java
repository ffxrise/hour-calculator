package com.fanfx.hourcalculator.exception;



public enum SysCodeEnums{
    /**
     * 成功
     */
    SUCCESS(0,"计算完成<br/>请在本软件所在文件夹查看Excel文件"),
    /**
     * 参数错误
     */
    ERROR_PARAM(4005,"参数错误"),
    /**
     * 未知错误
     */
    ERROR_UNKNOW(-1,"未知错误，请联系开发人...");

    private long code;
    private String msg;

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    SysCodeEnums(long code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
