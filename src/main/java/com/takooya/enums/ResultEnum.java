package com.takooya.enums;

import lombok.Getter;

public enum ResultEnum {
    //访问成功
    SUCCESS(200, "成功"),
    NO_QUARTZ_JOB_FOUND(6001, "定时任务Job没有找到"),
    SHORTAGE_MODIFY_TRIGGER_PARAM(6002, "缺少修改Trigger的参数，请检查Trigger的groupName和name"),
    NO_QUARTZ_TRIGGER_FOUND(6003, "定时任务Trigger没有找到"),
    QUARTZ_RESCHEDULE_JOB_EXCEPTION(6004, "重新关联job与trigger失败异常"),
    SHORTAGE_QUARTZ_REMOVE_PARAM(6005, "缺少删除Job的参数，请对job和trigger的name、group全部赋值，以保证删除的正确性！"),
    UNSCHEDULE_TRIGGER_FAIL(6006, "删除trigger失败"),
    DELETE_JOB_FAIL(6007, "删除job失败"),
    SCHEDULER_IS_SHUTDOWN(6008, "调度器处于关闭状态"),
    SCHEDULER_IS_STANDBY(6009, "调度器处于待机状态"),
    QUARTZ_NAME_DOT_ILLEGAL(6010, "定时任务名称含有多个特殊字符“.”");
    @Getter
    private int status;
    @Getter
    private String message;

    /**
     * 构造方法
     *
     * @param status  code
     * @param message info
     */
    ResultEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
