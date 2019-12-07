package com.fanfx.hourcalculator.vo;


import com.fanfx.hourcalculator.exception.SysCodeEnums;
import lombok.Data;

/**
 *封装json结果
 */
@Data
public class DataResult {
    private String msg;//描述
    private Object data;//数据
    private Long code; //500表示token失效



    public DataResult() {
    }

    public DataResult(SysCodeEnums code) {
        this.code = code.getCode();
        this.msg=code.getMsg();
    }

    public DataResult(SysCodeEnums code, Object data) {
        this.code = code.getCode();
        this.msg=code.getMsg();
        this.data = data;
    }


    public DataResult(String msg, Long code) {
        this.msg = msg;
        this.code = code;
    }

    public DataResult(String msg, Object data, Long code) {
        this.msg = msg;
        this.data = data;
        this.code = code;
    }
}
