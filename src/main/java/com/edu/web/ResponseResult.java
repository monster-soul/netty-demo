package com.edu.web;


import com.edu.enums.ResponseStatusEnum;
import com.edu.web.exception.BaseException;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 响应结果包装模型
 *
 * @author pengxiangjun
 * @since 2021/2/7 11:04
 **/

public class ResponseResult<T> {

    /**
     * 响应状态码
     */
    Integer code;

    /**
     * 提示信息
     */
    String msg;

    /**
     * 返回数据
     */
    T data;

    /**
     * 服务器响应时间戳
     */
    long timestamp;

    /**
     * 异常信息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Error error;

    public ResponseResult() {
    }

    public ResponseResult(Integer code, String msg, T data, long timestamp) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = timestamp;
    }

    //region Getter/Setter

    /**
     * 返回一个成功结果
     *
     * @param data
     * @param <TData>
     * @return
     */
    public static <TData> ResponseResult<TData> success(TData data) {
        ResponseResult<TData> responseResult = new ResponseResult<TData>(ResponseStatusEnum.RESPONSE_OK.getValue(), ResponseStatusEnum.RESPONSE_OK.getMsg(), data, System.currentTimeMillis());
        return responseResult;
    }

    /**
     * 返回一个成功结果
     *
     * @param <TData>
     * @return
     */
    public static <TData> ResponseResult<TData> success() {
        return success(null);
    }

    /**
     * 返回一个错误结果
     *
     * @param msg
     * @param <TData>
     * @return
     */
    public static <TData> ResponseResult<TData> fail(String msg) {
        if (msg == null || msg.isEmpty()) {
            msg = ResponseStatusEnum.RESPONSE_Error.getMsg();
        }
        ResponseResult<TData> responseResult = new ResponseResult<>(ResponseStatusEnum.RESPONSE_Error.getValue(), msg, null, System.currentTimeMillis());
        return responseResult;
    }

    /**
     * 返回一个错误结果
     *
     * @param <TData>
     * @return
     */
    public static <TData> ResponseResult<TData> fail() {
        return new ResponseResult<>(ResponseStatusEnum.RESPONSE_Error.getValue(), ResponseStatusEnum.RESPONSE_Error.getMsg(), null, System.currentTimeMillis());
    }

    /**
     * 返回一个错误结果
     *
     * @param <TData>
     * @return
     */
    public static <TData> ResponseResult<TData> fail(ResponseStatusEnum responseStatusEnum) {
        ResponseResult<TData> responseResult = new ResponseResult<>(responseStatusEnum.getValue(), responseStatusEnum.getMsg(), null, System.currentTimeMillis());
        return responseResult;
    }

    /**
     * 返回一个错误结果
     *
     * @param responseStatusEnum
     * @param msg
     * @param <TData>
     * @return
     */
    public static <TData> ResponseResult<TData> fail(ResponseStatusEnum responseStatusEnum, String msg) {
        if (StringUtils.isEmpty(msg)) {
            msg = responseStatusEnum.getMsg();
        }
        ResponseResult<TData> responseResult = new ResponseResult<>(responseStatusEnum.getValue(), msg, null, System.currentTimeMillis());
        return responseResult;
    }

    /**
     * 返回一个自定义结果
     *
     * @param code
     * @param msg
     * @param data
     * @param <TData>
     * @return
     */
    public static <TData> ResponseResult<TData> wrap(Integer code, String msg, TData data) {
        ResponseResult<TData> responseResult = new ResponseResult<>(code, msg, data, System.currentTimeMillis());
        return responseResult;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    //endregion

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Error getError() {
        return error;
    }

    public Error buildError(Throwable ex) {
        if (ex instanceof BaseException) {
            BaseException exception = (BaseException) ex;
            if (exception.getCode() > 0) {
                this.setCode(exception.getCode());
            }
        }
        Error error = new Error();
        error.setMessage(ex.getMessage());
        if (StringUtils.isEmpty(error.getMessage())) {
            error.setMessage(ex.toString());
        }
        if (ex.getStackTrace() != null && ex.getStackTrace().length > 0) {
            error.setStackTrace(String.join("   \r\n   ", Arrays.stream(ex.getStackTrace()).map(row -> "at " + row.getClassName() + "." + row.getMethodName() + "(" + row.getFileName() + ":" + row.getLineNumber() + ")").collect(Collectors.toList())));
        }
        while (ex.getCause() != null) {
            if (ex.getCause() != ex) {
                ex = ex.getCause();
            } else {
                break;
            }
            Error internalError = new Error();
            internalError.setMessage(ex.getMessage());
            if (StringUtils.isEmpty(internalError.getMessage())) {
                internalError.setMessage(ex.getClass().getName());
            }
            internalError.setStackTrace(String.join("   \r\n    ", Arrays.stream(ex.getStackTrace()).map(row -> "at " + row.getClassName() + "." + row.getMethodName() + "(" + row.getFileName() + ":" + row.getLineNumber() + ")").collect(Collectors.toList())));
            error.setInternalError(internalError);
            if (ex instanceof BaseException) {
                BaseException baseException = (BaseException) ex;
                if (!StringUtils.isEmpty(baseException.getFriendlyReminder())) {
                    this.setMsg(baseException.getFriendlyReminder());
                }
                this.setData((T) baseException.getData());
            }
        }
        this.error = error;
        return error;
    }

    /**
     * 错误信息
     */
    public static class Error {
        /**
         * 堆栈信息
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String stackTrace;
        /**
         * 错误消息
         */
        private String message;

        /**
         * 内部错误（引发此异常的异常）
         */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Error internalError;

        public Error() {

        }

        public String getStackTrace() {
            return stackTrace;
        }

        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Error getInternalError() {
            return internalError;
        }

        public void setInternalError(Error internalError) {
            this.internalError = internalError;
        }
    }
}
