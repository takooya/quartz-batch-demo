package com.takooya.quartz.service;

import com.takooya.quartz.dao.QuartzManagerBean;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.Map;

public interface QuartzService {

    /**
     * 获取当前Scheduler实例中所有Job的信息
     *
     * @return key-value形式的 job名称-cron表达式
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    Map<String, QuartzManagerBean> getInfo() throws SchedulerException;

    /**
     * 添加一个定时任务  （带参数）
     *
     * @param qmb {
     *            jobName          任务名
     *            jobGroupName     任务组名
     *            triggerName      触发器名
     *            triggerGroupName 触发器组名
     *            cls              任务
     *            time             时间设置，参考quartz说明文档
     *            }
     * @return 新增后的 Quartz Job 信息
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    QuartzManagerBean addJob(QuartzManagerBean qmb) throws SchedulerException;

    /**
     * 修改一个任务执行
     *
     * @param qmb{}
     * @return 修改后的 Quartz Job 信息
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    QuartzManagerBean modifyJob(QuartzManagerBean qmb) throws SchedulerException;

    /**
     * 移除一个任务
     *
     * @param qmb { jobName          任务名
     *            jobGroupName     任务组名
     *            triggerName      触发器名
     *            triggerGroupName 触发器组名
     *            }
     * @return 被移除的job信息
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    QuartzManagerBean removeJob(QuartzManagerBean qmb) throws SchedulerException;

    /**
     * 暂停定时任务
     *
     * @param qmb {jobName,jobGroupName}
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    void pauseJob(QuartzManagerBean qmb) throws SchedulerException;

    /**
     * 重启动定时任务
     *
     * @param qmb {jobName,jobGroupName}
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    void resumeJob(QuartzManagerBean qmb) throws SchedulerException;

    /**
     * 暂停所有定时任务
     *
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    void standbyJobs() throws SchedulerException;

    /**
     * 启动所有定时任务
     *
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    void startJobs() throws SchedulerException;

    /**
     * 关闭调度器
     *
     * @throws SchedulerException Base class for exceptions thrown by the Quartz <code>{@link Scheduler}</code>.
     */
    @Deprecated
    void shutdownJobs() throws SchedulerException;
}
