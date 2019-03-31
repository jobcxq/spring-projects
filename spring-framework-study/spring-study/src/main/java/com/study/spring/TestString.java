package com.study.spring;

public class TestString {
    public static void main(String[] args){
        String a = "a";
        String b = "b";
        String d = "a" + "b";
        String c = a + b;
        String e = new String("ab");
        String f = new String("a") + b;
        System.out.println(c);
        System.out.println(d);
        System.out.println(e);
        System.out.println(f);
        System.out.println("ok");
    }

}
