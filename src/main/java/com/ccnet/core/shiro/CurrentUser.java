package com.ccnet.core.shiro;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccnet.core.common.utils.base.Const;


@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {

    /**
     *         当前用户在request中的名字
     *
     * @return
     */
    String value() default Const.CURRENT_USER;

}
