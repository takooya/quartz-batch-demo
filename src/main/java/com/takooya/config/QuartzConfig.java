package com.takooya.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {
    private static final String QUARTZ_DATASOURCE_PREFIX = "spring.datasource.druid.quartz";

    /**
     * <p>@QuartzDataSource</p> 注解则是配置Quartz独立数据源的配置
     */
    @Bean
    @QuartzDataSource
    @ConfigurationProperties(prefix = QUARTZ_DATASOURCE_PREFIX)
    public DataSource quartzDataSource() {
        return new DruidDataSource();
    }
    
//    /**
//     * 配置JobFactory
//     *
//     * @param applicationContext 容器上下文
//     * @return jobFactory
//     */
//    @Bean
//    public JobFactory jobFactory(ApplicationContext applicationContext) {
//        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
//        jobFactory.setApplicationContext(applicationContext);
//        return jobFactory;
//    }

//    /**
//     * SchedulerFactoryBean这个类的真正作用提供了对org.quartz.Scheduler的创建与配置，并且会管理它的生命周期与Spring同步。
//     * org.quartz.Scheduler: 调度器。所有的调度都是由它控制。
//     *
//     * @param dataSource 为SchedulerFactory配置数据源
//     * @param jobFactory 为SchedulerFactory配置JobFactory
//     */
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean(
//            @Qualifier("quartzDataSource") DataSource dataSource,
//            JobFactory jobFactory) throws IOException {
//        SchedulerFactoryBean factory = new SchedulerFactoryBean();
//        factory.setOverwriteExistingJobs(true);
//        factory.setAutoStartup(true); //设置自行启动
//        factory.setDataSource(dataSource);
//        factory.setJobFactory(jobFactory);
//        factory.setQuartzProperties(quartzProperties());
//        return factory;
//    }

//    /**
//     * 从quartz.properties文件中读取Quartz配置属性
//     *
//     * @return quartz.properties
//     * @throws IOException io异常
//     */
//    @Bean
//    public Properties quartzProperties() throws IOException {
//        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
//        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
//        propertiesFactoryBean.afterPropertiesSet();
//        return propertiesFactoryBean.getObject();
//    }

//    /**
//     * 配置JobFactory,为quartz作业添加自动连接支持
//     */
//    public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
//            ApplicationContextAware {
//        private AutowireCapableBeanFactory beanFactory;
//
//        @Override
//        public void setApplicationContext(final ApplicationContext context) {
//            beanFactory = context.getAutowireCapableBeanFactory();
//        }
//
//        @Override
//        protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
//            final Object job = super.createJobInstance(bundle);
//            beanFactory.autowireBean(job);
//            return job;
//        }
//    }
}
