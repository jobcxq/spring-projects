package com.study.spring.xmlbeanfactory;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

public class TestXmlBeanFactory {

    public static void main(String[] args){

        final XmlBeanFactory xmlBeanFactory = new XmlBeanFactory(new ClassPathResource("ApplicationContext.xml"));

        Object object = xmlBeanFactory.getBean("Student");
        System.out.println(Thread.currentThread().getName() + ": " + object);

        System.out.println(xmlBeanFactory.getBean("User"));
        System.out.println("ok");

        new Thread(new Runnable() {
            public void run() {
                System.out.println(Thread.currentThread().getName() + ": " + xmlBeanFactory.getBean("User"));
            }
        }).start();

    }
}
