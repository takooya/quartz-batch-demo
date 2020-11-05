package com.takooya.quartz.dao;

import lombok.Data;
import org.quartz.Calendar;

import java.util.Date;

@Data
public class QuartzOriginBean {
    private Calendar calendar;
    private boolean recovering;
    private Object result;
    private JobDetailWrapper jobDetail;
    private Date fireTime;
    private Date nextFireTime;
    private Date scheduledFireTime;
    private TriggerWrapper trigger;
    private long jobRunTime;
}
