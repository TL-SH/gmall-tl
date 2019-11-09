package com.atguigu.gmall.index.annotation;

import java.lang.annotation.*;

/**
 * @author tanglei
 */
//注解作用在哪里
@Target({ElementType.METHOD})
//运行时注解
@Retention(RetentionPolicy.RUNTIME)
// 可文档
@Documented
public @interface GmallCache {
    /**
     * 缓存前缀
     * @return
     */
    String prefix() default "cache";

    /**
     * 单位是秒
     * @return
     */
    long timeout() default 300l;

    /**
     * 为了防止缓存雪崩,而设置随机时间和过期时间的范围
     * @return
     */
    long random() default 300l;

}
