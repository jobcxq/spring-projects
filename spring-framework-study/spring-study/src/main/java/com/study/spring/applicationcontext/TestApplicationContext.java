package com.study.spring.applicationcontext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestApplicationContext {

    public static void main(String[] args){

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("ApplicationContext.xml");
        Object object = applicationContext.getBean("Student");
        System.out.println(object);
    }

}
