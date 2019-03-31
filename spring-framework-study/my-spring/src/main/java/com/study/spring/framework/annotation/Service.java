package com.study.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author cnxqin
 * @desc
 * @date 2019/03/30 22:16
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {

    String value() default "";

}
