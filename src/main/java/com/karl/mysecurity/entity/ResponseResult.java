package org.karl.mytest01.entity;

import org.karl.mytest01.constant.ResponseStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ResponseResult implements Serializable {
    private int code;
    private String msg;
    private Object data;
    private LocalDateTime timestamp;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


    public static <T> ResponseResult ok(){
        return ok(ResponseStatus.OK.getMsg(),null);
    }

    public static <T> ResponseResult ok(T data){
        return ok(ResponseStatus.OK.getMsg(),data);
    }

    public static <T> ResponseResult ok(String msg,T data){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(ResponseStatus.OK.getCode());
        responseResult.setMsg(msg);
        responseResult.setData(data);
        responseResult.setTimestamp(LocalDateTime.now());
        return responseResult;
    }

    public static <T> ResponseResult fail(String msg){
        return fail(msg,null);
    }

    public static <T> ResponseResult fail(String msg,T data){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(ResponseStatus.FAIL.getCode());
        responseResult.setMsg(msg);
        responseResult.setData(data);
        responseResult.setTimestamp(LocalDateTime.now());
        return responseResult;
    }

    public static <T> ResponseResult error(String msg){
        return error(msg,null);
    }

    public static <T> ResponseResult error(String msg,T data){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(ResponseStatus.ERROR.getCode());
        responseResult.setMsg(msg);
        responseResult.setData(data);
        responseResult.setTimestamp(LocalDateTime.now());
        return responseResult;
    }

    public static <T> ResponseResult instance(ResponseStatus status){
        ResponseResult responseResult = new ResponseResult();
        responseResult.setCode(status.getCode());
        responseResult.setMsg(status.getMsg());
        responseResult.setData(null);
        responseResult.setTimestamp(LocalDateTime.now());
        return responseResult;
    }

    public boolean isFail(){
        return this.code != ResponseStatus.OK.getCode();
    }

    public boolean isSuccess() {
        return this.code == ResponseStatus.OK.getCode();
    }
}
