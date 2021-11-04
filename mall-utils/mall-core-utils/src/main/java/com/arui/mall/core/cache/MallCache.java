package com.arui.mall.core.cache;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * @author ...
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MallCache {
    /**
     * 前缀
     * @return
     */
    String prefix() default "";

    /**
     * 后缀
     * @return
     */
    String suffix() default "";

    /**
     * 分布式锁的后缀
     * @return
     */
    String suffixLock() default "";
}
