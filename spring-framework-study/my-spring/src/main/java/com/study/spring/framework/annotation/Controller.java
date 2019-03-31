package com.study.spring.framework.annotation;

import java.lang.annotation.*;

/**
 * @author cnxqin
 * @desc
 * @date 2019/03/30 22:12
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

    String value() default "";

}
