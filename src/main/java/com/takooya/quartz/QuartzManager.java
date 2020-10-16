package com.takooya.quartz;

import cn.hutool.core.map.MapUtil;
import com.takooya.quartz.dao.QuartzManagerBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * 定时任务管理类
 */
@Component
@Slf4j
public class QuartzManager {
    /**
     * 注入任务调度器
     */
    @Autowired
    private Scheduler sched;
    /**
     * 任务组
     */
    private static String JOB_GROUP_NAME = "ATAO_JOBGROUP";
    /**
     * 触发器组
     */
    private static String TRIGGER_GROUP_NAME = "ATAO_TRIGGERGROUP";

    /**
     * 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     *
     * @param jobName 任务名
     * @param cls     任务
     * @param time    cron表达式 时间设置，参考quartz说明文档
     */
    public void addJob(String jobName, Class<? extends Job> cls, String time) {
        addJob(jobName, JOB_GROUP_NAME,
                jobName, TRIGGER_GROUP_NAME, cls,
                time, null);
    }

    /**
     * 添加一个定时任务  （带参数）
     *
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     * @param cls              任务
     * @param time             时间设置，参考quartz说明文档
     */
    public void addJob(String jobName, String jobGroupName,
                       String triggerName, String triggerGroupName, Class<? extends Job> cls,
                       String time, Map<String, Object> parameter) {
        try {
            // 任务名，任务组，任务执行类
            //用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
            JobDetail jobDetail = JobBuilder.newJob(cls).withIdentity(jobName, jobGroupName).build();
            //传参数
            if (MapUtil.isNotEmpty(parameter)) {
                jobDetail.getJobDataMap().put("parameterList", parameter);
            }
            // 触发器
            CronTrigger trigger = TriggerBuilder
                    //创建一个新的TriggerBuilder来规范一个触发器
                    .newTrigger()
                    //给触发器起一个名字和组名
                    .withIdentity(triggerName, triggerGroupName)
                    .withSchedule(CronScheduleBuilder.cronSchedule(time))
                    .build();
            sched.scheduleJob(jobDetail, trigger);
            if (!sched.isShutdown()) {
                // 启动
                sched.start();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加一个定时任务
     *
     * @param qmb 定时任务参数
     */
    public void addJob(QuartzManagerBean qmb) {
        addJob(qmb.getJobName(), qmb.getJobGroupName(),
                qmb.getTriggerName(), qmb.getTriggerGroupName(),
                qmb.getCls(), qmb.getTime(), qmb.getParameter());
    }

    /**
     * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName 任务名
     * @param time    新的时间设置
     */
    public void modifyJobTime(String jobName, String time) {
        try {
            //通过触发器名和组名获取TriggerKey
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);
            //通过TriggerKey获取CronTrigger
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(time)) {
                //通过任务名和组名获取JobKey
                JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
                JobDetail jobDetail = sched.getJobDetail(jobKey);
                Class<? extends Job> objJobClass = jobDetail.getJobClass();
                removeJob(jobName);
                addJob(new QuartzManagerBean(jobName, objJobClass, time));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改一个任务的触发时间
     *
     * @param triggerName      任务名称
     * @param triggerGroupName 传过来的任务名称
     * @param time             更新后的时间规则
     */
    public void modifyJobTime(String triggerName, String triggerGroupName, String time) {
        try {
            //通过触发器名和组名获取TriggerKey
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            //通过TriggerKey获取CronTrigger
            CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(trigger.getCronExpression());
            if (!oldTime.equalsIgnoreCase(time)) {
                //重新构建trigger
                trigger = trigger.getTriggerBuilder()
                        .withIdentity(triggerKey)
                        .withSchedule(scheduleBuilder)
                        .withSchedule(CronScheduleBuilder.cronSchedule(time))
                        .build();
                //按新的trigger重新设置job执行
                sched.rescheduleJob(triggerKey, trigger);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     *
     * @param jobName 任务名称
     */
    public void removeJob(String jobName) {
        removeJob(jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME);
    }

    /**
     * 移除一个任务
     *
     * @param jobName          任务名
     * @param jobGroupName     任务组名
     * @param triggerName      触发器名
     * @param triggerGroupName 触发器组名
     */
    public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
        try {
            //通过触发器名和组名获取TriggerKey
            TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
            //通过任务名和组名获取JobKey
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            // 停止触发器
            sched.pauseTrigger(triggerKey);
            // 移除触发器
            sched.unscheduleJob(triggerKey);
            // 删除任务
            sched.deleteJob(jobKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 启动所有定时任务
     */
    public void startJobs() {
        try {
            sched.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            if (!sched.isShutdown()) {
                sched.shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取当前Scheduler实例中所有Job的信息
     *
     * @return key-value形式的 job名称-cron表达式
     * @throws SchedulerException 获取jobGroupNames发生的异常
     */
    public Map<String, String> getInfo() throws SchedulerException {
        List<String> jobGroupNames = sched.getJobGroupNames();
        AtomicInteger i = new AtomicInteger();
        Set<JobKey> allJobKeys = jobGroupNames.stream().collect(HashSet::new,
                (jobKeys, s) -> {
                    GroupMatcher<JobKey> jobKeyGroupMatcher = GroupMatcher.jobGroupEquals(s);
                    try {
                        jobKeys.addAll(sched.getJobKeys(jobKeyGroupMatcher));
                    } catch (SchedulerException e) {
                        jobKeys.add(new JobKey("ErrorKey" + i.getAndIncrement(), s));
                    }
                },
                (BiConsumer<Set<JobKey>, Set<JobKey>>) Set::addAll);
        return allJobKeys.stream().collect(HashMap::new, (BiConsumer<Map<String, String>, JobKey>) (result, jobKey) -> {
            if (jobKey.getName().contains("ErrorKey")) {
                result.put(jobKey.getName(), "null");
            } else {
                try {
                    JobDetail jobDetail = sched.getJobDetail(jobKey);
                    List<? extends Trigger> triggersOfJob = sched.getTriggersOfJob(jobKey);
                    Map<String, String> temp = triggersOfJob.stream().collect(HashMap::new,
                            (BiConsumer<Map<String, String>, Trigger>) (target, trigger) -> {
                                if (trigger instanceof CronTrigger) {
                                    String cronExpression = ((CronTrigger) trigger).getCronExpression();
                                    target.put(jobKey.getGroup() + "." + jobKey.getName(), cronExpression);
                                } else {
                                    target.put(jobKey.getGroup() + "." + jobKey.getName(), trigger.getDescription());
                                }
                            }, Map::putAll);
                    result.putAll(temp);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                    result.put(jobKey.getName(), "SchedulerException:" + e.getMessage());
                }
            }
        }, Map::putAll);
    }
}