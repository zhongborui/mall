package com.arui.mall.feign.client;

import com.arui.mall.common.result.R;
import com.arui.mall.model.search.SearchParam;
import com.arui.mall.model.search.SearchResponseVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    /**
     * 增加热度
     * @param skuId
     * @return
     */
    @GetMapping("/search/incrHostScore/{skuId}")
    public R incrHostScore(@PathVariable Long skuId);

    /**
     * 商品搜索
     * @param searchParam
     * @return
     */
    @PostMapping("/search/product")
    public R searchProduct(@RequestBody SearchParam searchParam);
}
