package com.joyfishs.system.annotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CheckVerify {
    public String phone() default "";
    public String type() default "";
    public String code() default "";
}
