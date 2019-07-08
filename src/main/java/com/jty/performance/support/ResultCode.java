package com.jty.performance.support;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回结果枚举类
 *
 * @author Jason
 * @since 2018/12/17 13:18
 */
@Getter
public enum ResultCode {

    /* 成功状态码 */
    SUCCESS(1, "成功"),

    /* 参数错误：10001-19999 */
    PARAM_IS_INVALID(10001, "参数无效"),
    PARAM_IS_BLANK(10002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(10003, "参数类型错误"),
    PARAM_NOT_COMPLETE(10004, "参数缺失"),

    /* 用户错误：20001-29999*/
    USER_NOT_LOGGED_IN(20001, "用户未登录"),
    USER_LOGIN_ERROR(20002, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(20003, "账号已被禁用"),
    USER_NOT_EXIST(20004, "用户不存在"),
    USER_HAS_EXISTED(20005, "用户已存在"),
    USER_HAS_NOT_PERMISSION(20006, "用户没有权限"),
    ORGANIZATION_IS_DIFFERENT(20007, "用户没有权限管理另一个机构的内容"),
    PHONE_CODE_ERROR(20008, "手机验证码错误"),
    VALIDATE_CODE_ERROR(20009, "验证码错误"),
    PASSWORD_ERROR(20010, "密码错误"),
    EMAIL_HAS_EXISTED(20011, "邮箱已存在"),
    TEACHER_NOT_EXIST(20012, "老师账号不存在"),
    SCHOOL_HAS_EXISTED(20013, "学校已存在"),
    MISOPERATION(20019, "错误操作"),
    ROLE_HAS_EXIST(20018, "角色已存在"),
    DEPARTMENT_HAS_EXIST(20020, "部门已存在"),
    CLIENT_NOT_EXIST(20100, "用户API调用信息不存在"),
    GOAL_HAS_TASK(20101, "删除失败：目标已关联任务"),
    GOAL_TASKING(20103, "操作失败：目标下有未完成任务"),
    REWARD_HAS_GET(20102, "悬赏领取失败：悬赏已被领取"),

    /* 业务错误：30001-39999 */
    SPECIFIED_QUESTIONED_USER_NOT_EXIST(30001, "某业务出现问题"),
    OVER_DAY_LIMIT(30002, "超过每日请求上限，请明天再来~"),
    TEACHER_ADD_PAPER_OVER_SCHOOL_LIMIT(30006, "新增老师超过学校限制次数"),
    COMPONENT_TRYOUT_OVER_SCHOOL_LIMIT(300007, "产品试用超过学校限制次数"),


    INIT_PAPER_QUESTION_COUNT_LIMIT(30008, "试题数量超过99"),
    INIT_PAPER_QUESTION_REPEAT(30009, "参数重复"),
    SCHOOL_NAME_REPEAT(30010, "学校已存在"),


    /* 系统错误：40001-49999 */
    SYSTEM_INNER_ERROR(40001, "系统繁忙，请稍后重试"),

    /* 数据错误：50001-599999 */
    RESULT_DATA_NONE(50001, "数据未找到"),
    DATA_IS_WRONG(50002, "数据有误"),
    DATA_ALREADY_EXISTED(50003, "数据已存在"),
    DATA_IS_COMPRESS(50004, "数据是压缩文件"),
    TYPE_IS_WRONG(50005, "类型错误"),

    /* 接口错误：60001-69999 */
    INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
    INTERFACE_OUTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
    INTERFACE_FORBID_VISIT(60003, "该接口禁止访问"),
    INTERFACE_ADDRESS_INVALID(60004, "接口地址无效"),
    INTERFACE_REQUEST_TIMEOUT(60005, "接口请求超时"),
    INTERFACE_EXCEED_LOAD(60006, "接口负载过高"),

    INTERFACE_INVOKE_NO_ACCESS(60010, "已经超过接口调用次数限制"),
    INTERFACE_INVOKE_IS_DISABLE(60011, "接口资源已被禁用"),
    INTERFACE_INVOKE_IS_EXPIRED(60012, "接口资源已过期"),

    ES_PULL_DATE_ERROR(61001, "ES推送数据错误"),
    ES_IO_ERROR(61002, "ES连接IO错误"),

    SCORE_BEYOND(80000, "转赠积分超出预算"),
    TASK_SCORE_BEYOND(80001, "任务积分不匹配"),
    USER_CHECKED(80002, "用户已签到"),

    TEMPLATE_PARAM_IS_BLANK(70012, "模板参数为空"),

    STAFF_TASK_UNDONE(70015, "该员工有尚未完成的任务"),
    /* 权限错误：70001-79999 */
    PERMISSION_NO_ACCESS(70001, "无访问权限");


    private Integer code;

    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }

    public static String getMessage(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.message;
            }
        }
        return name;
    }

    public static Integer getCode(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.code;
            }
        }
        return null;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return this.name();
    }

    //校验重复的code值
    public static void main(String[] args) {
        ResultCode[] apiResultCodes = ResultCode.values();
        List<Integer> codeList = new ArrayList<>();
        for (ResultCode apiResultCode : apiResultCodes) {
            if (codeList.contains(apiResultCode.code)) {
                System.out.println(apiResultCode.code);
            } else {
                codeList.add(apiResultCode.code());
            }
        }
    }
}
