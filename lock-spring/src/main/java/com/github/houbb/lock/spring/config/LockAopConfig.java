package com.github.houbb.lock.spring.config;

import com.github.houbb.common.cache.api.service.ICommonCacheService;
import com.github.houbb.id.api.Id;
import com.github.houbb.lock.api.core.ILockKeyFormat;
import com.github.houbb.lock.api.core.ILockReleaseFailHandler;
import com.github.houbb.lock.core.bs.LockBs;
import com.github.houbb.lock.spring.annotation.EnableLock;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * aop 配置
 *
 * @author binbin.hou
 * @since 0.0.2
 */
@Configuration
@ComponentScan(basePackages = "com.github.houbb.lock.spring")
@Import({LockBeanConfig.class})
public class LockAopConfig implements ImportAware, BeanFactoryPostProcessor {

    @Bean("lockBs")
    public LockBs lockBs() {
        ICommonCacheService commonCacheService = beanFactory.getBean(enableLockAttributes.getString("cache"), ICommonCacheService.class);
        Id id = beanFactory.getBean(enableLockAttributes.getString("id"), Id.class);
        ILockKeyFormat lockKeyFormat = beanFactory.getBean(enableLockAttributes.getString("lockKeyFormat"), ILockKeyFormat.class);
        ILockReleaseFailHandler lockReleaseFailHandler = beanFactory.getBean(enableLockAttributes.getString("lockReleaseFailHandler"), ILockReleaseFailHandler.class);

        return LockBs.newInstance()
                .cache(commonCacheService)
                .id(id)
                .lockKeyFormat(lockKeyFormat)
                .lockReleaseFailHandler(lockReleaseFailHandler);
    }

    /**
     * 属性信息
     */
    private AnnotationAttributes enableLockAttributes;

    /**
     * bean 工厂
     *
     * @since 0.0.5
     */
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setImportMetadata(AnnotationMetadata annotationMetadata) {
        enableLockAttributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(EnableLock.class.getName(), false));
        if (enableLockAttributes == null) {
            throw new IllegalArgumentException(
                    "@EnableLock is not present on importing class " + annotationMetadata.getClassName());
        }
    }

}
