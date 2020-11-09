package com.takooya.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author takooya
 */
@Configuration
@EnableBatchProcessing
@Import(DataSourceAutoConfiguration.class)
@Slf4j
public class BatchConfig {
//    @Autowired
//    private JobRegistry jobRegistry;
//    @Autowired
//    private ApplicationContext applicationContext;
//
//    @Bean
//    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor() {
//        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
//        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
//        return jobRegistryBeanPostProcessor;
//    }
}
