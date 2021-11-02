package com.arui.mall.product.service.impl;

import com.arui.mall.core.constant.RedisConstant;
import com.arui.mall.model.pojo.entity.SkuImage;
import com.arui.mall.model.pojo.entity.SkuInfo;
import com.arui.mall.model.pojo.entity.SkuPlatformPropertyValue;
import com.arui.mall.model.pojo.entity.SkuSalePropertyValue;
import com.arui.mall.model.pojo.vo.SkuInfoVO;
import com.arui.mall.product.mapper.SkuInfoMapper;
import com.arui.mall.product.service.SkuImageService;
import com.arui.mall.product.service.SkuInfoService;
import com.arui.mall.product.service.SkuPlatformPropertyValueService;
import com.arui.mall.product.service.SkuSalePropertyValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jdk.nashorn.internal.ir.CallNode;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 库存单元表 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo> implements SkuInfoService {

    @Resource
    private SkuInfoService skuInfoService;

    @Resource
    private SkuPlatformPropertyValueService skuPlatformPropertyValueService;

    @Resource
    private SkuSalePropertyValueService skuSalePropertyValueService;

    @Resource
    private SkuImageService skuImageService;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSkuInfo(SkuInfoVO skuInfoVO) {
        // 插入sku_info表
        SkuInfo skuInfo = new SkuInfo();
        BeanUtils.copyProperties(skuInfoVO, skuInfo);
        Long productId = skuInfoVO.getSpuId();
        skuInfo.setSpuId(productId);
        baseMapper.insert(skuInfo);

        // 返回sku主键
        Long id = skuInfo.getId();

        // 插入sku_platform_property_value表
        List<SkuPlatformPropertyValue> skuPlatformPropertyValueList = skuInfoVO.getSkuPlatformPropertyValueList();
        for (SkuPlatformPropertyValue skuPlatformPropertyValue : skuPlatformPropertyValueList) {
            skuPlatformPropertyValue.setSkuId(id);
        }
        skuPlatformPropertyValueService.saveBatch(skuPlatformPropertyValueList);

        // 插入sku_sale_property_value
        List<SkuSalePropertyValue> skuSalePropertyValueList = skuInfoVO.getSkuSalePropertyValueList();
        for (SkuSalePropertyValue skuSalePropertyValue : skuSalePropertyValueList) {
            skuSalePropertyValue.setSkuId(id);
            skuSalePropertyValue.setProductId(productId);
        }
        skuSalePropertyValueService.saveBatch(skuSalePropertyValueList);

        // 插入sku_image表
        List<SkuImage> skuImageList = skuInfoVO.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(id);
        }
        skuImageService.saveBatch(skuImageList);
    }

    /**
     * 通过redisson提供分布式锁解决线程安全问题
     * @param skuId
     * @return
     */
    @Override
    public SkuInfoVO getSkuDetailById(Long skuId) {
        String skuKey = RedisConstant.SKUKEY_PREFIX + skuId + RedisConstant.SKUKEY_SUFFIX;
        SkuInfoVO skuInfoVO = (SkuInfoVO) redisTemplate.opsForValue().get(skuKey);

        if (skuInfoVO == null){
            // 缓存中数据为空，查数据库

            // 初始化锁
            String skuLockKey = RedisConstant.SKUKEY_PREFIX + skuId + RedisConstant.SKULOCK_SUFFIX;
            RLock lock = redissonClient.getLock(skuLockKey);
            try {
                // 尝试获得锁
                boolean acquireLock = lock.tryLock(RedisConstant.WAITTIN_GET_LOCK_TIME, RedisConstant.LOCK_EXPIRE_TIME, TimeUnit.SECONDS);
                if (acquireLock){
                    // 查数据库
                    SkuInfoVO skuInfoVOFromDB = getSkuInfoVOFromDB(skuId);
                    // 设置空值防止缓存穿透
                    if (skuInfoVOFromDB == null){
                        SkuInfoVO emptySkuInVO = new SkuInfoVO();
                        redisTemplate.opsForValue().set(skuKey, emptySkuInVO, RedisConstant.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                        return emptySkuInVO;
                    }

                    // 将数据放到缓存中
                    redisTemplate.opsForValue().set(skuKey, skuInfoVOFromDB, RedisConstant.SKUKEY_TEMPORARY_TIMEOUT, TimeUnit.SECONDS);

                    return skuInfoVOFromDB;
                }else {
                    //自旋
                    return getSkuDetailById(skuId);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }else {
            // 缓存中有数据，直接返回
            return skuInfoVO;
        }
        return null;

    }

    private SkuInfoVO getSkuInfoVOFromDB(Long skuId) {
        SkuInfoVO skuInfoVO = new SkuInfoVO();

        SkuInfo skuInfo = baseMapper.selectById(skuId);
        if (skuInfoVO != null){
            BeanUtils.copyProperties(skuInfo, skuInfoVO);

            QueryWrapper<SkuImage> skuImageQueryWrapper = new QueryWrapper<>();
            skuImageQueryWrapper.eq("sku_id", skuId);
            List<SkuImage> list = skuImageService.list(skuImageQueryWrapper);
            skuInfoVO.setSkuImageList(list);
        }

        return skuInfoVO;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        QueryWrapper<SkuInfo> skuInfoQueryWrapper = new QueryWrapper<>();
        skuInfoQueryWrapper.select("price").eq("id", skuId);
        BigDecimal price = (BigDecimal) skuInfoService.getObj(skuInfoQueryWrapper);
        return price;
    }
}
