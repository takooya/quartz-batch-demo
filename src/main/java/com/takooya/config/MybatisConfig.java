package com.takooya.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.util.Properties;

/**
 * @author takooya
 */
@Slf4j
@Configuration
@MapperScan(basePackages = MybatisConfig.MAPPER_PACKAGE, sqlSessionFactoryRef = MybatisConfig.SESSION_FACTORY)
public class MybatisConfig {
    /*
    @Value("${mybatis.queryLimit}")
    private String queryLimit;
    */

    private static final String DATASOURCE_NAME = "dataSource";

    static final String SESSION_FACTORY = "sqlSessionFactory";

    static final String SESSION_TEMPLATE = "sqlSessionTemplate";

    /**
     * mapper类的包路径
     */
    static final String MAPPER_PACKAGE = "com.takooya.mybatis.mapper";

    /**
     * 数据源配置的前缀，必须与application.properties中配置的对应数据源的前缀一致
     */
    private static final String BUSINESS_DATASOURCE_PREFIX = "spring.datasource.druid.business";

    @Value("${mybatis.type-aliases-package}")
    private String TYPE_ALIASES_PACKAGE;

    /**
     * 自定义mapper的xml文件路径
     */
    @Value("${mybatis.mapper-locations}")
    private String MAPPER_LOCATIONS;

    @Value("${mybatis.configuration.log-impl}")
    private String LOG_IMPL;

    @Primary
    @Bean(name = DATASOURCE_NAME)
    @ConfigurationProperties(prefix = BUSINESS_DATASOURCE_PREFIX)
    public DruidDataSource druidDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 配置Mybatis环境
     */
    @Primary
    @Bean(name = SESSION_FACTORY)
    public SqlSessionFactory sqlSessionFactory() {
        log.info("配置SqlSessionFactory开始");
        final SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(druidDataSource());
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            // 自定义mapper的xml文件地址，当通用mapper提供的默认功能无法满足我们的需求时，可以自己添加实现，与mybatis写mapper一样
            sessionFactoryBean.setMapperLocations(resolver.getResources(MAPPER_LOCATIONS));
            org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
            Properties properties = new Properties();
            /*properties.put("queryLimit", queryLimit);*/
            configuration.setVariables(properties);
            configuration.setMapUnderscoreToCamelCase(true);
            //noinspection unchecked
            configuration.setLogImpl((Class<? extends Log>) Class.forName(LOG_IMPL));
            sessionFactoryBean.setConfiguration(configuration);
            sessionFactoryBean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
            return sessionFactoryBean.getObject();
        } catch (Exception e) {
            log.error("配置SqlSessionFactory失败，error:{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Primary
    @Bean(name = SESSION_TEMPLATE)
    public SqlSessionTemplate sqlSessionTemplate() {
        return new SqlSessionTemplate(sqlSessionFactory(), ExecutorType.BATCH);
    }
}
