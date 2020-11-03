package com.takooya.quartz.dao;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.StrUtil;
import com.takooya.quartz.DynamicJob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.Job;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * quartz执行的基础信息Bean
 *
 * @author takooya
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuartzManagerBean implements Serializable {
    @NotBlank
    private String jobName;
    private String jobGroupName;
    private String triggerName;
    private String triggerGroupName;
    private Class<? extends Job> cls = DynamicJob.class;
    private String clsName = DynamicJob.class.getName();
    private Map<String, Object> parameter;

    @NotBlank
    private String time;

    public QuartzManagerBean(@NotBlank String jobName, @NotBlank String time) {
        this.jobName = jobName;
        this.time = time;
    }

    public QuartzManagerBean(@NotBlank String jobName, String clsName, @NotBlank String time) {
        this.jobName = jobName;
        if (!DynamicJob.class.getName().equals(clsName)) {
            this.clsName = clsName;
            this.cls = (Class<? extends Job>) ClassLoaderUtil.loadClass(clsName, Job.class.getClassLoader(), false);
        }
        this.time = time;
    }

    public QuartzManagerBean(@NotBlank String jobName, Class<? extends Job> cls, @NotBlank String time) {
        this.jobName = jobName;
        if (!DynamicJob.class.equals(cls)) {
            this.cls = cls;
            this.clsName = cls.getName();
        }
        this.time = time;
    }

    public QuartzManagerBean(QuartzManagerBean source) {
        BeanUtil.copyProperties(source, this);
    }

    public Class<? extends Job> getCls() {
        if (cls == null && StrUtil.isBlank(clsName)) {
            cls = DynamicJob.class;
            clsName = cls.getName();
            return cls;
        }
        if (cls == null && StrUtil.isNotBlank(clsName)) {
            String[] split = DynamicJob.class.getName().split("\\.");
            if (ArrayUtil.contains(split, clsName)) {
                cls = DynamicJob.class;
                clsName = cls.getName();
            } else {
                cls = getClass(clsName);
            }
            return cls;
        }
        if (cls != null && StrUtil.isBlank(clsName)) {
            clsName = cls.getName();
            return cls;
        }
        if (cls != null && StrUtil.isNotBlank(clsName)) {
            String[] split = cls.getName().split("\\.");
            if (cls.getName().equals(clsName) || ArrayUtil.contains(split, clsName)) {
                return cls;
            } else {
                throw new RuntimeException("类名与类不匹配");
            }
        }
        throw new RuntimeException("未考虑到的情况发生！");
    }

    public void setCls(Class<? extends Job> cls) {
        this.cls = cls;
        this.clsName = cls.getName();
    }

    public String getClsName() {
        if (cls != null) {
            this.clsName = cls.getName();
        }
        return clsName;
    }

    public void setClsName(String clsName) {
        this.cls = getClass(clsName);
        this.clsName = this.cls.getName();
    }

    public String getTriggerName() {
        if (StrUtil.isBlank(triggerName)) {
            return jobName;
        }
        return triggerName;
    }

    private Class<? extends Job> getClass(@NotBlank String clsName) {
        if (!clsName.contains("com.takooya.quartz.")) {
            clsName = "com.takooya.quartz." + clsName;
        }
        return (Class<? extends Job>) ClassLoaderUtil.loadClass(clsName, Job.class.getClassLoader(), false);
    }
}
