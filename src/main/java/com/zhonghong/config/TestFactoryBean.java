package com.zhonghong.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author zhonghong
 */
@Component
public class TestFactoryBean implements FactoryBean<Test> {

    private String name;

//    public TestFactoryBean(String name) {
//        this.name = name;
//    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Test getObject() {
        return new Test(name);
    }

    @Override
    public Class<?> getObjectType() {
        return Test.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

class Test {
    private String name;

    Test(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Test{" +
                "name='" + name + '\'' +
                '}';
    }
}
