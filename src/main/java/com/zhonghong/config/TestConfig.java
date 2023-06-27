package com.zhonghong.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhonghong
 */
@Configuration
public class TestConfig {

    @Resource
    private ApplicationContext applicationContext;

    @PostConstruct
    private void init() {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) autowireCapableBeanFactory;

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(TestFactoryBean.class);
        beanDefinition.setAttribute("name", "zhonghong");

        defaultListableBeanFactory.registerBeanDefinition("TestClass", beanDefinition);

        defaultListableBeanFactory.registerSingleton("TestClass", new Test("test"));

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Test.class);
        beanDefinitionBuilder.addConstructorArgValue("999");
        beanDefinitionBuilder.addPropertyValue("name", "666");
        defaultListableBeanFactory.registerBeanDefinition("myTest", beanDefinitionBuilder.getRawBeanDefinition());

        Test test = (Test) applicationContext.getBean("myTest");
        System.out.println("test ==> " + test);
    }

//    @Bean
//    public TestFactoryBean myTest() {
//        return new TestFactoryBean("test");
//    }
}
