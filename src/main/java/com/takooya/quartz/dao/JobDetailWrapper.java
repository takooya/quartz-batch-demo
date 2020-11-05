package com.takooya.quartz.dao;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.impl.JobDetailImpl;

public class JobDetailWrapper extends JobDetailImpl implements JobDetail {
    @Override
    public JobBuilder getJobBuilder() {
        return null;
    }
}
