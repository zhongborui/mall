package com.arui.mall.core.cache;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.arui.mall.core.constant.RedisConstant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author ...
 */
@Component
@Aspect
public class MallCachingAspect {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Around("@annotation(com.arui.mall.core.cache.MallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint target) {
        // 拼接key
        Object[] args = target.getArgs();
        String param = Arrays.toString(args);
        MethodSignature signature = (MethodSignature) target.getSignature();
        Method method = signature.getMethod();
        MallCache annotation = method.getAnnotation(MallCache.class);
        String prefix = annotation.prefix();
        String suffix = annotation.suffix();
        String suffixLock = annotation.suffixLock();
        String key = prefix + param + suffix;

        // 查询缓存中是否有数据
        Object obj = redisTemplate.opsForValue().get(key);

        if (obj == null){
            // 缓存中没有数据，查DB
            try {
                obj = target.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }

            // redis set操作加上Redisson分布式锁
            // 初始化锁
            String lockKey = prefix + param + suffixLock;
            RLock rLock = redissonClient.getLock(lockKey);

            // 尝试获得锁
            try {
                boolean acquireLock = rLock.tryLock(RedisConstant.WAITTIN_GET_LOCK_TIME, RedisConstant.LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
                if (acquireLock){
                    // 获取了锁, 可以操作
                    // 判断obj是否为null，防止缓存穿透
                    if (obj == null){
                        // 设置空值，防止缓存穿透
                        Object emptyObj = new Object();
                        redisTemplate.opsForValue().set(key, emptyObj, RedisConstant.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                        return emptyObj;
                    }

                    // obj不为null，直接存到缓存中，返回
                    redisTemplate.opsForValue().set(key, obj, RedisConstant.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);
                    return obj;
                }else {
                    // 没有获得锁,睡眠一段时间，自旋
                    Thread.sleep(50);
                    return cacheAroundAdvice(target);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                // 释放锁
                rLock.unlock();
            }


        }else {
            // 缓存中有数据，直接返回
            return obj;
        }
        return null;

    }

}
