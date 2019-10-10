package cn.jianchengwang.tl.storage.autoconfigure;

import cn.jianchengwang.tl.storage.adapter.*;
import cn.jianchengwang.tl.storage.autoconfigure.properties.StorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by wjc on 2019/8/23
 **/
@Slf4j
@Configuration
@EnableConfigurationProperties(StorageProperties.class)
@ConditionalOnExpression("${storage.enable:true}")
public class StorageAutoConfiguration {

    /**
     * 自动配置FS存储适配器
     */
    @Bean
    @ConditionalOnProperty(name = "storage.storeType", havingValue = "fs")
    @ConditionalOnMissingBean
    public StorageAdapter autoConfigFSAdapter(StorageProperties properties) {
        properties.getFs().validate();
        return new FSAdapter(properties.getFs());
    }

    /**
     * 自动配置OSS存储适配器
     */
    @Bean
    @ConditionalOnProperty(name = "storage.storeType", havingValue = "oss")
    @ConditionalOnMissingBean
    public StorageAdapter autoConfigOSSAdapter(StorageProperties properties) {
        properties.getOss().validate();
       return new OSSAdapter(properties.getOss());
    }

    /**
     * 自动配置KODO存储适配器
     */
    @Bean
    @ConditionalOnProperty(name = "storage.storeType", havingValue = "kodo")
    @ConditionalOnMissingBean
    public StorageAdapter autoConfigKODOAdapter(StorageProperties properties) {
        properties.getKodo().validate();
        return new KODOAdapter(properties.getKodo());
    }

    /**
     * 自动配置S3存储适配器
     */
    @Bean
    @ConditionalOnProperty(name = "storage.storeType", havingValue = "s3")
    @ConditionalOnMissingBean
    public StorageAdapter autoConfigS3Adapter(StorageProperties properties) {
        properties.getS3().validate();
        return new S3Adapter(properties.getS3());
    }

    /**
     * 自动配置AZURE存储适配器
     */
    @Bean
    @ConditionalOnProperty(name = "storage.storeType", havingValue = "azure")
    @ConditionalOnMissingBean
    public StorageAdapter autoConfigAZUREAdapter(StorageProperties properties) {
        properties.getAzure().validate();
        return new AZUREAdatper(properties.getAzure());
    }
}
