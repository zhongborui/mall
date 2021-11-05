package com.arui.mall.feign.client;

import com.arui.mall.common.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author ...
 */
@FeignClient(value = "mall-search")
public interface SearchFeignClient {
    /**
     * es上架
     * @param skuId
     * @return
     */
    @GetMapping("/search/onSale/{skuId}")
    public R onSale(@PathVariable Long skuId);

    /**
     * es下架
     * @param skuId
     * @return
     */
    @GetMapping("/search/offSale/{skuId}")
    public R offSale(@PathVariable Long skuId);
}
