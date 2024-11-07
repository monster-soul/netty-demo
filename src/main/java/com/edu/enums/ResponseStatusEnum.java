package com.edu.enums;


/**
 * 响应结果状态枚举
 *
 * @author DuHai
 * @since 2021/2/7 11:05
 **/

public enum ResponseStatusEnum {

    /**
     * 操作成功
     */
    RESPONSE_OK(ResponseStatusEnum.RESPONSE_OK_Value, "操作成功"),

    /**
     * 操作失败
     */
    RESPONSE_Error(ResponseStatusEnum.RESPONSE_Error_Value, "操作失败"),

    /**
     * 无效签名
     */
    RESPONSE_InvalidSignature(ResponseStatusEnum.RESPONSE_InvalidSignature_Value, "无效签名"),

    /**
     * 未登录或登录超时，请重新登录
     */
    RESPONSE_NoLogin(ResponseStatusEnum.RESPONSE_NoLogin_Value, "未登录或登录超时，请重新登录"),

    /**
     * 会话超时
     */
    RESPONSE_SessionTimeout(ResponseStatusEnum.RESPONSE_SessionTimeout_Value, "您的会话已超时，请重新登录"),

    /**
     * 因未修改密码，已被禁止访问
     */
    RESPONSE_PasswordNotChanged(ResponseStatusEnum.RESPONSE_PasswordNotChanged_Value, "因安全策略限制，请先修改密码再进行操作"),

    /**
     * 因未修改密码，已被禁止访问
     */
    RESPONSE_PasswordNotChangedForgetThePassword(ResponseStatusEnum.RESPONSE_PasswordNotChangedForgetThePassword_Value, "因安全策略限制，请先修改密码再进行操作,当前正在使用忘记密码功能"),

    /**
     * 因权限问题，已被禁止访问
     */
    RESPONSE_Unauthorized(ResponseStatusEnum.RESPONSE_Unauthorized_Value, "因权限问题，已被禁止访问"),

    /**
     * 资源不存在
     */
    RESPONSE_NotFound(ResponseStatusEnum.RESPONSE_NotFound_Value, "资源不存在"),

    /**
     * 服务繁忙，请稍后再试
     */
    RESPONSE_RetryWith(ResponseStatusEnum.RESPONSE_RetryWith_Value, "服务繁忙，请稍后再试"),

    /**
     * 数据已存在，请重新输入
     */
    RESPONSE_DUPLICATE_DATA(ResponseStatusEnum.RESPONSE_DUPLICATE_DATA_Value, "数据已存在，请重新输入"),

    /**
     * 存在多余的数据，请检查相关数据，进行清理
     */
    RESPONSE_TooMany(ResponseStatusEnum.RESPONSE_TooMany_Value, "存在多余的数据，请检查相关数据，进行清理"),

    /**
     * 输入参数验证错误，请检查输入内容
     */
    RESPONSE_ValidationException(ResponseStatusEnum.RESPONSE_ValidationException_Value, "输入参数验证错误，请检查输入内容"),

    /**
     * 提交文本格式错误，无法正确序列化，请检查输入内容
     */
    RESPONSE_SerializeException(ResponseStatusEnum.RESPONSE_SerializeException_Value, "提交文本格式错误，无法正确序列化，请检查输入内容"),

    /**
     * 当前正在处理中，请勿重复提交
     */
    RESPONSE_Locked(ResponseStatusEnum.RESPONSE_Locked_Value, "当前正在处理中，请勿重复提交"),

    /**
     * 服务器错误
     */
    RESPONSE_ServerError(ResponseStatusEnum.RESPONSE_ServerError_Value, "服务异常，请联系后端开发人员"),

    /**
     * 网关异常
     */
    RESPONSE_BAD_GATEWAY(ResponseStatusEnum.RESPONSE_BAD_GATEWAY_Value, "网关异常"),

    /**
     * 服务暂不可用
     */
    RESPONSE_SERVICE_UNAVAILABLE(ResponseStatusEnum.RESPONSE_SERVICE_UNAVAILABLE_Value, "服务暂不可用"),

    /**
     * 网关超时
     */
    RESPONSE_GATEWAY_TIMEOUT(ResponseStatusEnum.RESPONSE_GATEWAY_TIMEOUT_Value, "网关超时");

    private final String msg;
    private final int value;

    ResponseStatusEnum(int value, String msg) {
        this.msg = msg;
        this.value = value;
    }

    public String getMsg() {
        return this.msg;
    }

    public int getValue() {
        return this.value;
    }

    //region value常量定义

    /**
     * 操作成功
     */
    public static final int RESPONSE_OK_Value = 200;

    /**
     * 操作失败
     */
    public static final int RESPONSE_Error_Value = 400;

    /**
     * 无效签名
     */
    public static final int RESPONSE_InvalidSignature_Value = 40001;

    /**
     * 未登录或登录超时，请重新登录
     */
    public static final int RESPONSE_NoLogin_Value = 401;

    /**
     * 会话超时
     */
    public static final int RESPONSE_SessionTimeout_Value = 40101;

    /**
     * 因未修改密码，已被禁止访问
     */
    public static final int RESPONSE_PasswordNotChanged_Value = 402;

    /**
     * 因未修改密码，已被禁止访问
     */
    public static final int RESPONSE_PasswordNotChangedForgetThePassword_Value = 40201;

    /**
     * 因权限问题，已被禁止访问
     */
    public static final int RESPONSE_Unauthorized_Value = 403;

    /**
     * 资源不存在
     */
    public static final int RESPONSE_NotFound_Value = 404;

    /**
     * 服务繁忙，请稍后再试
     */
    public static final int RESPONSE_RetryWith_Value = 449;

    /**
     * 数据已存在，请重新输入
     */
    public static final int RESPONSE_DUPLICATE_DATA_Value = 450;

    /**
     * 存在多余的数据，请检查相关数据，进行清理
     */
    public static final int RESPONSE_TooMany_Value = 451;

    /**
     * 输入参数验证错误，请检查输入内容
     */
    public static final int RESPONSE_ValidationException_Value = 422;

    /**
     * 提交文本格式错误，无法正确序列化，请检查输入内容
     */
    public static final int RESPONSE_SerializeException_Value = 42201;

    /**
     * 当前正在处理中，请勿重复提交
     */
    public static final int RESPONSE_Locked_Value = 423;

    /**
     * 服务器错误
     */
    public static final int RESPONSE_ServerError_Value = 500;

    /**
     * 网关异常
     */
    public static final int RESPONSE_BAD_GATEWAY_Value = 502;

    /**
     * 服务暂不可用
     */
    public static final int RESPONSE_SERVICE_UNAVAILABLE_Value = 503;

    /**
     * 网关超时
     */
    public static final int RESPONSE_GATEWAY_TIMEOUT_Value = 504;

    //endregion
}
