package com.edu.web.exception;

import java.util.Map;

/**
 * 所有自定义基类
 *
 * @author pengxiangjun
 * @since 2024/3/27 14:24
 **/

public class BaseException extends RuntimeException {

    /**
     * 友好的提示内容
     * 可用于前端直接展示
     */
    public String friendlyReminder;

    /**
     * 异常代码
     */
    public int code;

    /**
     * 携带的数据
     */
    private Map<String, String> data;

    /**
     * 无参构造函数
     */
    @Deprecated
    public BaseException() {
        super();
    }

    /**
     * @param friendlyReminder 友好的提示信息，可直接用于前端提示
     */
    @Deprecated
    public BaseException(String friendlyReminder) {
        this(friendlyReminder, null, true, true, friendlyReminder, null);
    }

    /**
     * @param message 非友好提示消息，主要方便运维人员快速定位问题
     * @param cause   内部异常信息原因，引发当前异常的其他异常
     */
    @Deprecated
    public BaseException(String message, Throwable cause) {
        this(message, cause, true, true, null, null);
    }

    /**
     * @param message 非友好提示消息，主要方便运维人员快速定位问题
     * @param data    异常相关数据
     */
    public BaseException(String message, Map<String, String> data) {
        this(message, null, true, true, null, data);
    }

    /**
     * @param message 非友好提示消息，主要方便运维人员快速定位问题
     * @param cause   内部异常信息原因，引发当前异常的其他异常
     * @param data    异常相关数据
     */
    @Deprecated
    public BaseException(String message, Throwable cause, Map<String, String> data) {
        this(message, cause, true, true, null, data);
    }

    /**
     * @param message          非友好提示消息，主要方便运维人员快速定位问题
     * @param cause            内部异常信息原因，引发当前异常的其他异常
     * @param friendlyReminder 友好的提示信息，可直接用于前端提示
     */
    @Deprecated
    public BaseException(String message, Throwable cause, String friendlyReminder) {
        this(message, cause, true, true, friendlyReminder, null);
    }

    /**
     * @param message          异常信息
     * @param cause            内部异常信息原因，引发当前异常的其他异常
     * @param friendlyReminder 友好的提示信息，可直接用于前端提示
     * @param data             异常数据
     */
    public BaseException(String message, Throwable cause, String friendlyReminder, Map<String, String> data) {
        this(message, cause, true, true, friendlyReminder, data);
    }

    /**
     * @param message            异常信息
     * @param cause              内部异常信息原因，引发当前异常的其他异常
     * @param enableSuppression
     * @param writableStackTrace
     * @param friendlyReminder   友好的提示信息，可直接用于前端提示
     */
    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String friendlyReminder) {
        this(message, cause, enableSuppression, writableStackTrace, friendlyReminder, null);
    }

    /**
     * @param message            异常信息
     * @param cause              内部异常信息原因，引发当前异常的其他异常
     * @param enableSuppression
     * @param writableStackTrace
     * @param friendlyReminder
     * @param data               异常相关数据
     */
    public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String friendlyReminder, Map<String, String> data) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.friendlyReminder = friendlyReminder;
        this.data = data;
    }

    public String getFriendlyReminder() {
        return friendlyReminder;
    }

    public BaseException setFriendlyReminder(String friendlyReminder) {
        this.friendlyReminder = friendlyReminder;
        return this;
    }

    public int getCode() {
        return code;
    }

    public BaseException setCode(int code) {
        this.code = code;
        return this;
    }

    public Map<String, String> getData() {
        return data;
    }

    public BaseException setData(Map<String, String> data) {
        this.data = data;
        return this;
    }
}
