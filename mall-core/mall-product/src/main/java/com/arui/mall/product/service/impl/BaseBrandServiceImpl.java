package com.arui.mall.product.service.impl;

import com.arui.mall.model.pojo.entity.BaseBrand;
import com.arui.mall.product.mapper.BaseBrandMapper;
import com.arui.mall.product.service.BaseBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 品牌表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class BaseBrandServiceImpl extends ServiceImpl<BaseBrandMapper, BaseBrand> implements BaseBrandService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     *
     */
    @Override
    public  void testLocalLock() {
//        Boolean getLock = redisTemplate.opsForValue().setIfAbsent("lock", "ok", 2, TimeUnit.SECONDS);
//        if (getLock){
//            doBusiness();
//            redisTemplate.delete("lock");
//        }else {
//            // 自旋
//            testLocalLock();
//        }


        //设置锁的值为一个UUID
        String uuid = UUID.randomUUID().toString();
        // 设置过期时间
        boolean acquireLock = redisTemplate.opsForValue().setIfAbsent("lock",uuid, 3,TimeUnit.SECONDS);
        if(acquireLock){

            // 业务代码
            doBusiness();

            // lua脚本 保证 redis的判断key是否自己的锁和删除锁操作的原子性
            //  定义一个lua 脚本
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            //  准备执行lua 脚本
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            //  将lua脚本放入DefaultRedisScript 对象中
            redisScript.setScriptText(script);
            //  设置DefaultRedisScript 这个对象的泛型
            redisScript.setResultType(Long.class);
            //  执行删除
            redisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);
        }else{
            //自旋
            testLocalLock();
        }


    }

    private void doBusiness() {
        String num = (String) redisTemplate.opsForValue().get("num");
        if (StringUtils.isEmpty(num)){
            redisTemplate.opsForValue().set("num", "1");
        }else {
            int i = Integer.parseInt(num);
            ++i;
            redisTemplate.opsForValue().set("num", String.valueOf(i));
        }
    }
}
