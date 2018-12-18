/**
 * BrandBigData.com Inc. Copyright (c) 2016 All Rights Reserved.
 */
package com.youliang.ResponseModel;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author xumin
 * @version $Id:ResponseModel.java, v0.1 2017/3/7 0003 下午 3:05 xumin
 */
@Configurable(autowire = Autowire.BY_TYPE, preConstruction = true)
public class ResponseModel<T> implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;
    private static final int STATUS_SUCCESS = 200;
    private static final int NOT_MODIFY = 304;
    private static final int STATUS_ERROR = 500;
    private static final int STATUS_NOT_FOUND = 404;
    private static final int STATUS_ACCESS_FORBIDDEN = 403;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String message;
    private T data;

    private ResponseModel(int status, String messageCode, T data) {
        this.status = status;
        this.message = messageCode;
        this.data = data;
    }

    private ResponseModel() {
    }

    public static <T> ResponseModel<T> SUCCESS() {
        return SUCCESS("message.success.default", null);
    }

    public static <T> ResponseModel<T> SUCCESS(String messageCode) {
        return SUCCESS(messageCode, null);
    }

    public static <T> ResponseModel<T> SUCCESS(T data) {
        return SUCCESS("message.success.default", data);
    }

    public static <T> ResponseModel<T> SUCCESS(String messageCode, T data) {
        return new ResponseModel<>(STATUS_SUCCESS, messageCode, data);
    }

    public static <T> ResponseModel<T> NOT_MODIFY() {
        return NOT_MODIFY("message.object.not.modify", null);
    }

    public static <T> ResponseModel<T> NOT_MODIFY(String messageCode, T data) {
        return new ResponseModel<T>(NOT_MODIFY, messageCode, data);
    }

    public static <T> ResponseModel<T> NOT_MODIFY(String messageCode) {
        return new ResponseModel<T>(NOT_MODIFY, messageCode, null);
    }

    public static <T> ResponseModel<T> ERROR() {
        return ERROR("error.internal.error");
    }

    public static <T> ResponseModel<T> ERROR(String messageCode) {
        return ERROR(STATUS_ERROR, messageCode, null);
    }

    public static <T> ResponseModel<T> ERROR(String messageCode, T data) {
        return ERROR(STATUS_ERROR, messageCode, data);
    }

    public static <T> ResponseModel<T> ERROR(int status, String message) {
        ResponseModel<T> model = new ResponseModel<>();
        model.status = status;
        model.message = message;
        return model;
    }

    private static <T> ResponseModel<T> ERROR(int status, String messageCode, T data) {
        return new ResponseModel<>(status, messageCode, data);
    }

    public static <T> ResponseModel<T> NOT_FOUND(String messageCode) {
        return ERROR(STATUS_NOT_FOUND, messageCode, null);
    }

    public static <T> ResponseModel<T> NOT_FOUND() {
        return NOT_FOUND("error.object.not.found");
    }

    public static <T> ResponseModel<T> ACCESS_FORBIDDEN() {
        return ERROR(STATUS_ACCESS_FORBIDDEN, "error.access.forbidden", null);
    }

    public static <T> ResponseModel<T> SESSION_INVALID() {
        return ERROR(STATUS_ACCESS_FORBIDDEN, "error.access.session.invalid", null);
    }

    public static <T> ResponseModel<T> warp(ResponseModel<T> orgin) {
        return orgin;
    }

    public boolean isSuccess() {
        return this.status == STATUS_SUCCESS ? true : false;
    }

    @Override
    public String toString() {
        return "ResponseModel{" + "timestamp=" + timestamp + ", status=" + status + ", message='"
                + message + '\'' + ", data=" + data + '}';
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
