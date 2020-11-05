package com.takooya.quartz.dao;

import org.quartz.CronTrigger;
import org.quartz.ScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.impl.triggers.CronTriggerImpl;

public class TriggerWrapper extends CronTriggerImpl implements CronTrigger {
    @Override
    public ScheduleBuilder<CronTrigger> getScheduleBuilder() {
        return null;
    }

    @Override
    public TriggerBuilder<CronTrigger> getTriggerBuilder() {
        return null;
    }
}
