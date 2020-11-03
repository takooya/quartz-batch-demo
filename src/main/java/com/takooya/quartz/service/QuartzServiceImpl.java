package com.takooya.quartz.service;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.takooya.enums.ResultEnum;
import com.takooya.exception.QuartzManageException;
import com.takooya.quartz.dao.QuartzManagerBean;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 定时任务管理类
 *
 * @author takooya
 */
@Component
@Slf4j
public class QuartzServiceImpl implements QuartzService {
    /**
     * 注入任务调度器
     */
    @SuppressWarnings("SpellCheckingInspection")
    private final Scheduler sched;
    /**
     * 任务组
     */
    @SuppressWarnings("FieldCanBeLocal")
    private static String JOB_GROUP_NAME = "DEFAULT";
    /**
     * 触发器组
     */
    @SuppressWarnings("FieldCanBeLocal")
    private static String TRIGGER_GROUP_NAME = "DEFAULT";

    public QuartzServiceImpl(Scheduler sched) {
        this.sched = sched;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, QuartzManagerBean> getInfo() throws SchedulerException {
        List<String> jobGroupNames = sched.getJobGroupNames();
        AtomicInteger i = new AtomicInteger();
        Set<JobKey> allJobKeys = jobGroupNames.stream().collect(HashSet::new,
                (jobKeys, groupName) -> {
                    GroupMatcher<JobKey> jobKeyGroupMatcher = GroupMatcher.jobGroupEquals(groupName);
                    try {
                        jobKeys.addAll(sched.getJobKeys(jobKeyGroupMatcher));
                    } catch (SchedulerException e) {
                        jobKeys.add(new JobKey("ErrorKey" + i.getAndIncrement(), groupName));
                    }
                },
                Set::addAll);
        return allJobKeys.stream().collect(HashMap::new, (result, jobKey) -> {
            if (jobKey.getName().contains("ErrorKey")) {
                result.put(jobKey.getGroup() + StrUtil.DOT + jobKey.getName(), null);
            }
            try {
                JobDetail jobDetail = sched.getJobDetail(jobKey);
                List<? extends Trigger> triggersOfJob = sched.getTriggersOfJob(jobKey);
                QuartzManagerBean single = new QuartzManagerBean();
                single.setClsName(jobDetail.getJobClass().getName());
                single.setJobGroupName(jobDetail.getKey().getGroup());
                single.setJobName(jobDetail.getKey().getName());
                single.setParameter(jobDetail.getJobDataMap().getWrappedMap());
                AtomicInteger triggerNum = new AtomicInteger();
                Map<String, QuartzManagerBean> temp = triggersOfJob.stream().collect(HashMap::new,
                        (qmbTarget, trigger) -> {
                            QuartzManagerBean tempResult = new QuartzManagerBean(single);
                            if (trigger instanceof CronTrigger) {
                                String cronExpression = ((CronTrigger) trigger).getCronExpression();
                                tempResult.setTriggerGroupName(trigger.getKey().getGroup());
                                tempResult.setTriggerName(trigger.getKey().getName());
                                tempResult.setTime(cronExpression);
                            }
                            qmbTarget.put(jobKey.getGroup() + "." + jobKey.getName() + triggerNum.incrementAndGet(), tempResult);
                        }, Map::putAll);
                result.putAll(temp);
            } catch (SchedulerException e) {
                e.printStackTrace();
                result.put(jobKey.getGroup() + "." + jobKey.getName(), null);
            }
        }, Map::putAll);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuartzManagerBean addJob(QuartzManagerBean qmb) throws SchedulerException {
        this.fillShortageInfo(qmb);
        // 任务名，任务组，任务执行类
        //用于描叙Job实现类及其他的一些静态信息，构建一个作业实例
        JobDetail jobDetail = JobBuilder.newJob(qmb.getCls())
                .withIdentity(qmb.getJobName(), qmb.getJobGroupName())
                .build();
        //传参数
        if (MapUtil.isNotEmpty(qmb.getParameter())) {
            qmb.getParameter().forEach((s, o) -> jobDetail.getJobDataMap().put(s, o));
        }
        // 触发器  创建一个新的TriggerBuilder来规范一个触发器
        CronTrigger trigger = TriggerBuilder.newTrigger()
                //给触发器起一个名字和组名
                .withIdentity(qmb.getTriggerName(), qmb.getJobGroupName())
                .withSchedule(CronScheduleBuilder.cronSchedule(qmb.getTime()))
                .build();
        sched.scheduleJob(jobDetail, trigger);
        if (!sched.isShutdown()) {
            // 启动
            sched.start();
        }
        return qmb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuartzManagerBean modifyJob(QuartzManagerBean qmb) throws SchedulerException {
        this.fillShortageInfo(qmb);
        this.getJobDetail(qmb);
        if (StrUtil.isNotBlank(qmb.getTime())) {
            this.modifyJobTime(qmb);
        }
        if (qmb.getCls() != null) {
            this.modifyJobClz(qmb);
        }
        return qmb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuartzManagerBean removeJob(QuartzManagerBean qmb) throws SchedulerException {
        Trigger trigger = this.getTrigger(qmb);
        JobDetail jobDetail = this.getJobDetail(qmb);
        // 停止触发器
        sched.pauseTrigger(trigger.getKey());
        // 移除触发器
        boolean unscheduleFlag = sched.unscheduleJob(trigger.getKey());
        if (!unscheduleFlag) {
            throw new QuartzManageException(ResultEnum.UNSCHEDULE_TRIGGER_FAIL);
        }
        // 删除任务
        boolean deleteFlag = sched.deleteJob(jobDetail.getKey());
        if (!deleteFlag) {
            log.error("删除job后返回的deleteFlag是false");
        }
        return qmb;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void standbyJobs() throws SchedulerException {
        if (sched.isInStandbyMode()) {
            throw new QuartzManageException(ResultEnum.SCHEDULER_IS_STANDBY);
        }
        sched.standby();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startJobs() throws SchedulerException {
        sched.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdownJobs() throws SchedulerException {
        if (!sched.isShutdown()) {
            sched.shutdown(true);
        } else {
            throw new QuartzManageException(ResultEnum.SCHEDULER_IS_SHUTDOWN);
        }
    }

    /**
     * 修改一个任务的执行任务
     *
     * @param qmb { triggerName    任务名称
     *            triggerGroupName 传过来的任务名称
     *            parameter        更新后的时间规则
     *            cls              待执行任务
     *            }
     */
    private void modifyJobClz(QuartzManagerBean qmb) throws SchedulerException {
        Map<String, Object> parameter = qmb.getParameter();
        JobDataMap jobDataMap;
        if (MapUtil.isEmpty(parameter)) {
            JobDetail jobDetail = this.getJobDetail(qmb);
            jobDataMap = jobDetail.getJobDataMap();
        } else {
            jobDataMap = new JobDataMap(qmb.getParameter());
        }
        this.removeJob(qmb);
        qmb.setParameter(jobDataMap);
        this.addJob(qmb);
    }

    /**
     * 修改一个任务的触发时间
     *
     * @param qmb { triggerName      任务名称
     *            triggerGroupName 传过来的任务名称
     *            time             更新后的时间规则
     *            }
     */
    private void modifyJobTime(QuartzManagerBean qmb) throws SchedulerException {
        CronTrigger trigger = getTrigger(qmb);
        String oldTime = trigger.getCronExpression();
        if (!oldTime.equalsIgnoreCase(qmb.getTime())) {
            //重新构建trigger
            trigger = trigger.getTriggerBuilder()
                    .withIdentity(trigger.getKey())
                    .withSchedule(CronScheduleBuilder.cronSchedule(qmb.getTime()))
                    .build();
            //按新的trigger重新设置job执行
            try {
                sched.rescheduleJob(trigger.getKey(), trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
                throw new QuartzManageException(ResultEnum.QUARTZ_RESCHEDULE_JOB_EXCEPTION);
            }
        }
    }

    /**
     * 补充缺少的信息
     *
     * @param qmb 待补充对象
     */
    private void fillShortageInfo(QuartzManagerBean qmb) {
        if (StrUtil.isBlank(qmb.getJobGroupName())) {
            if (qmb.getJobName().contains(StrUtil.DOT)) {
                String[] split = qmb.getJobName().split("\\.");
                if (split.length != 2) {
                    throw new QuartzManageException(ResultEnum.QUARTZ_NAME_DOT_ILLEGAL);
                }
                qmb.setJobGroupName(split[0]);
                qmb.setJobName(split[1]);
            } else {
                qmb.setJobGroupName(JOB_GROUP_NAME);
            }
        }
        if (StrUtil.isBlank(qmb.getTriggerName())) {
            qmb.setTriggerName(qmb.getJobName());
            qmb.setTriggerGroupName(qmb.getJobGroupName());
        } else if (StrUtil.isBlank(qmb.getTriggerGroupName())) {
            if (qmb.getTriggerName().contains(StrUtil.DOT)) {
                String[] split = qmb.getTriggerName().split("\\.");
                if (split.length != 2) {
                    throw new QuartzManageException(ResultEnum.QUARTZ_NAME_DOT_ILLEGAL);
                }
                qmb.setTriggerGroupName(split[0]);
                qmb.setTriggerName(split[1]);
            } else {
                qmb.setTriggerGroupName(TRIGGER_GROUP_NAME);
            }
        }
    }

    /**
     * 验证并获取jobDetail
     *
     * @param qmb{}
     * @return JobDetail
     */
    private JobDetail getJobDetail(QuartzManagerBean qmb) throws SchedulerException {
        //通过任务名和组名获取JobKey
        JobKey jobKey = JobKey.jobKey(qmb.getJobName(), qmb.getJobGroupName());
        JobDetail jobDetail = sched.getJobDetail(jobKey);
        if (ObjectUtil.isEmpty(jobDetail)) {
            throw new QuartzManageException(ResultEnum.NO_QUARTZ_JOB_FOUND);
        }
        return jobDetail;
    }

    /**
     * 验证并获取trigger
     *
     * @param qmb{}
     * @return CronTrigger
     */
    private CronTrigger getTrigger(QuartzManagerBean qmb) throws SchedulerException {
        //通过触发器名和组名获取TriggerKey
        TriggerKey triggerKey = TriggerKey.triggerKey(qmb.getTriggerName(), qmb.getJobGroupName());
        //通过TriggerKey获取CronTrigger
        CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
        if (trigger == null) {
            throw new QuartzManageException(ResultEnum.NO_QUARTZ_TRIGGER_FOUND);
        }
        return trigger;
    }
}