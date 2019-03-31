package com.study.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author cnxqin
 * @desc
 * @date 2019/03/30 22:26
 */

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestParam {

    String value() default "";

    boolean required() default false;

}
