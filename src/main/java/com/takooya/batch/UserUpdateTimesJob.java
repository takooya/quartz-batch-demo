package com.takooya.batch;

import com.takooya.mybatis.dao.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author takooya
 */
@Component
@Slf4j
public class UserUpdateTimesJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionFactory;
    private final SqlSessionTemplate sqlSessionTemplate;

    public UserUpdateTimesJob(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            @Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory,
            @Qualifier("sqlSessionTemplate") SqlSessionTemplate sqlSessionTemplate) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.sqlSessionFactory = sqlSessionFactory;
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    @Bean(name = "userUpdateTimes")
    public Job job() {
        return jobBuilderFactory.get("userUpdateTimesJob")
                .start(userUpdateTimesStep())
                .build();
    }

    public Step userUpdateTimesStep() {
        return stepBuilderFactory.get("userUpdateTimesStep")
                .<User, User>chunk(100)
                .faultTolerant().retryLimit(3).retry(Exception.class)
                .reader(itemReader())
                .writer(itemWriter())
                .build();
    }

    public ItemReader<? extends User> itemReader() {
        MyBatisPagingItemReader<User> pagingItemReader = new MyBatisPagingItemReader<>();
        /*MyBatisCursorItemReader<User> cursorItemReader = new MyBatisCursorItemReader<>();*/
        try {
            pagingItemReader.setSaveState(true);
            pagingItemReader.setName("userMapper");
            // 每次只读取5条数据
            /*cursorItemReader.setMaxItemCount(5);*/
            /*cursorItemReader.setCurrentItemCount(3);*/
            pagingItemReader.setSqlSessionFactory(sqlSessionFactory);
            pagingItemReader.setQueryId("select");
            pagingItemReader.setParameterValues(null);
            pagingItemReader.afterPropertiesSet();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pagingItemReader;
    }

    public ItemWriter<? super User> itemWriter() {
        MyBatisBatchItemWriter<User> itemWriter = new MyBatisBatchItemWriter<>();
        itemWriter.setStatementId("updateOne");
        itemWriter.setSqlSessionTemplate(sqlSessionTemplate);
        itemWriter.setSqlSessionFactory(sqlSessionFactory);
        itemWriter.setItemToParameterConverter(user -> {
            user.setTimes(1 + user.getTimes());
            return user;
        });
        itemWriter.setAssertUpdates(true);
        itemWriter.afterPropertiesSet();
        return itemWriter;
    }
}
