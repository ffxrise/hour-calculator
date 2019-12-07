package com.fanfx.hourcalculator;

import com.fanfx.hourcalculator.vo.DataResult;
import com.fanfx.hourcalculator.exception.ServiceException;
import com.fanfx.hourcalculator.exception.SysCodeEnums;
import com.google.common.base.Strings;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public DataResult handleException(BindException e) {
        DataResult dataResult = new DataResult();
        StringBuilder sb = new StringBuilder();
        dataResult.setCode(SysCodeEnums.ERROR_PARAM.getCode());
        for (ObjectError allError : e.getAllErrors()) {
            if (!Strings.isNullOrEmpty(sb.toString())) sb.append(",");
            sb.append(allError.getDefaultMessage());
        }
        dataResult.setMsg(sb.toString());
        return dataResult;
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public DataResult handleException(ServiceException e) {
        DataResult dataResult = new DataResult();
        dataResult.setCode(e.getCode());
        dataResult.setMsg(e.getMsg());
        return dataResult;
    }

//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public DataResult handleException(Exception e) {
//        DataResult dataResult = new DataResult();
//        dataResult.setCode(SysCodeEnums.ERROR_UNKNOW.getCode());
//        dataResult.setMsg(e.getMessage());
//        return dataResult;
//    }
}
