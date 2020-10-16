package com.takooya.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DynamicJob1 implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Trigger trigger = context.getTrigger();
        if (trigger instanceof CronTrigger) {
            log.info("[-DynamicJob-]{}'s cron is {}",
                    trigger.getKey(),
                    ((CronTrigger) trigger).getCronExpression());
        }
    }
}