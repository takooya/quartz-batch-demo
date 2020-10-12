package com.takooya.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DynamicJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        JobDetail jobDetail = context.getJobDetail();
        String cronExpression = jobDetail.getJobDataMap().getString("cronExpression");
        Trigger trigger = context.getTrigger();
        log.info("[-DynamicJob-]{}'s cron is {}", trigger.getKey(), cronExpression);
    }
}
