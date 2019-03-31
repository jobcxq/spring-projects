package com.study.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author cnxqin
 * @desc
 * @date 2019/03/30 22:13
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String value() default "";

    String method() default "";
}
