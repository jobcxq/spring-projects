package com.study.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestLambda {

    public static void main(String[] args){
        List<String> list = Arrays.asList(new String[]{"陈小钦","柯喵喵","夏铮","柯达"});
        List keFamily = new ArrayList();    //保存姓“柯”的人名
        //for 循环操作
        for(String name : list){
            if(name.startsWith("柯")){
                keFamily.add(name);
                System.out.println("for 循环过滤：" + name);
            }
        }
        System.out.println("for -> result:" + keFamily);
        System.out.println("=======================");
        //lambda 操作
        keFamily = list.stream().filter((name) -> name.startsWith("柯")).collect(Collectors.toList());
        System.out.println("lambda -> result:" + keFamily);
        keFamily.stream().forEach((name) -> {
            System.out.println("lambda 遍历集合：" + name);
        });

    }
}
