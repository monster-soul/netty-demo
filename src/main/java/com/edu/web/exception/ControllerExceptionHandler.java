//package com.edu.web.exception;
//
//import com.edu.enums.ResponseStatusEnum;
//import com.edu.web.ResponseResult;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.exc.InvalidFormatException;
//import com.fasterxml.jackson.databind.exc.MismatchedInputException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.util.StringUtils;
//import org.springframework.validation.FieldError;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * 自定义异常处理拦截器
// * 仅用于处理控制器层的异常
// *
// * @author pengxiangjun
// * @since 2021/2/25 17:52
// **/
//@ControllerAdvice(annotations = ResponseBody.class)
//@Order(Ordered.LOWEST_PRECEDENCE)
//public class ControllerExceptionHandler {
//
//    private final Logger logger = LoggerFactory.getLogger(this.getClass());
//    private final ExceptionManager exceptionManager;
//
//    /**
//     * 禁用字段验证错误消息包装
//     */
//    @Value("${xc.validation.disableFieldErrorMessageWrap:false}")
//    private boolean disableFieldErrorMessageWrap;
//
//    public ControllerExceptionHandler(
//            ExceptionManager exceptionManager
//    ) {
//        this.exceptionManager = exceptionManager;
//    }
//
//    //region 自定义异常处理
//
//    /**
//     * 捕捉{@link BaseException}
//     *
//     * @return
//     */
//    @ResponseStatus(HttpStatus.OK)
//    @ExceptionHandler(BaseException.class)
//    @ResponseBody
//    public ResponseResult<Object> handleBaseException(BaseException ex) {
//        this.logger.error("执行异常" + ex.getMessage());
//        int code = ex.getCode();
//        if (code <= 0) {
//            code = ResponseStatusEnum.RESPONSE_Error.getValue();
//        }
//        String msg = ex.getFriendlyReminder();
//        if (StringUtils.isEmpty(msg)) {
//            msg = ex.getMessage();
//        }
//        if (StringUtils.isEmpty(msg)) {
//            msg = ResponseStatusEnum.RESPONSE_Error.getMsg();
//        }
//        ResponseResult<Object> result = ResponseResult.wrap(code, msg, ex.getData());
//        this.fillErrorStack(result, ex);
//        return result;
//    }
//
//    //endregion
//
//    /**
//     * 捕捉参数校验异常
//     *
//     * @param ex
//     * @return
//     */
//    @ResponseStatus(HttpStatus.OK)
//    @ExceptionHandler(value = MethodArgumentNotValidException.class)
//    @ResponseBody
//    public ResponseResult<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
//        this.logger.error("输入参数异常", ex);
//        ResponseResult<Object> result = null;
//        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
//        if (fieldErrors != null && fieldErrors.size() > 0) {
//            if (fieldErrors.size() > 1) {
//                fieldErrors = fieldErrors.stream().sorted(Comparator.comparing(FieldError::getField)).collect(Collectors.toList());
//            }
//            List<String> errors = new ArrayList<>(fieldErrors.size());
//            fieldErrors.forEach(fieldError -> {
//                if (this.disableFieldErrorMessageWrap) {
//                    errors.add(fieldError.getDefaultMessage());
//                } else {
//                    errors.add("(" + fieldError.getField() + ")" + fieldError.getDefaultMessage());
//                }
//            });
//            result = ResponseResult.fail(ResponseStatusEnum.RESPONSE_ValidationException, "输入参数无效：" + String.join(";", errors));
//        } else {
//            result = ResponseResult.fail(ResponseStatusEnum.RESPONSE_ValidationException, "输入参数无效，请检查输入内容！");
//        }
//
//        this.fillErrorStack(result, ex);
//        return result;
//    }
//
//    /**
//     * 捕捉参数格式化错误异常
//     *
//     * @param ex
//     * @return
//     */
//    @ResponseStatus(HttpStatus.OK)
//    @ExceptionHandler(value = HttpMessageNotReadableException.class)
//    @ResponseBody
//    public ResponseResult<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
//        this.logger.error("输入参数异常", ex);
//        ResponseResult<Object> result;
//        if (ex.getCause() instanceof MismatchedInputException) {
//            MismatchedInputException mismatchedInputException = (MismatchedInputException) ex.getCause();
//            result = ResponseResult.wrap(ResponseStatusEnum.RESPONSE_ValidationException.getValue(), "输入参数类型无效，这通常是因为实际输入参数类型和接收类型不匹配导致的！", mismatchedInputException.getTargetType().getSimpleName());
//        } else if (ex.getCause() instanceof InvalidFormatException) {
//            InvalidFormatException invalidFormatException = (InvalidFormatException) ex.getCause();
//            String inputValue = invalidFormatException.getValue().toString();
//            String targetType = invalidFormatException.getTargetType().getName();
//            List<String> fieldNames = new ArrayList<>(1);
//            List<JsonMappingException.Reference> paths = invalidFormatException.getPath();
//            for (JsonMappingException.Reference reference : paths) {
//                fieldNames.add(reference.getFieldName());
//            }
//            result = ResponseResult.fail(ResponseStatusEnum.RESPONSE_ValidationException,
//                    "输入参数无效：" + String.join(",", fieldNames) + "的值" + inputValue + "不是有效的" + targetType + "类型");
//        } else if (ex.getCause() instanceof JsonMappingException) {
//            JsonMappingException jsonMappingException = (JsonMappingException) ex.getCause();
//            return ResponseResult.fail(ResponseStatusEnum.RESPONSE_ValidationException,
//                    jsonMappingException.getOriginalMessage());
//        } else {
//            result = ResponseResult.fail(ResponseStatusEnum.RESPONSE_ValidationException, "输入参数无效，这通常是因为没有传入任何请求体导致的");
//        }
//        this.fillErrorStack(result, ex);
//        return result;
//    }
//
//    /**
//     * 捕捉数据库操作异常
//     *
//     * @param ex
//     * @return
//     */
//    @ResponseStatus(HttpStatus.OK)
//    @ExceptionHandler(value = SQLException.class)
//    @ResponseBody
//    public ResponseResult<Object> handleSqlException(SQLException ex) {
//        this.logger.error("数据库访问异常", ex);
//        ResponseResult<Object> result = ResponseResult.fail(ResponseStatusEnum.RESPONSE_ServerError);
//        this.fillErrorStack(result, ex);
//        return result;
//    }
//
//    /**
//     * 全局异常捕捉处理
//     */
//    @ResponseBody
//    @ExceptionHandler(value = Throwable.class)
//    public ResponseResult<Object> errorHandler(Throwable ex) {
//        ResponseResult<Object> result = this.exceptionManager.handler(ex);
//        if (result == null) {
//            result = ResponseResult.fail(ResponseStatusEnum.RESPONSE_ServerError);
//        } else {
//            this.logger.error("未知异常", ex);
//        }
//        this.fillErrorStack(result, ex);
//        return result;
//    }
//
//    /**
//     * 填充异常堆栈信息
//     *
//     * @param result
//     * @param ex
//     */
//    protected void fillErrorStack(ResponseResult result, Throwable ex) {
//        if (Boolean.FALSE) {
//            result.buildError(ex);
//        }
//    }
//
//}
