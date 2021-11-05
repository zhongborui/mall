package com.arui.mall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.model.pojo.entity.BaseCategoryView;
import com.arui.mall.product.mapper.BaseCategoryViewMapper;
import com.arui.mall.product.service.BaseCategoryViewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * VIEW 服务实现类
 * </p>
 *
 * @author ...
 * @since 2021-10-28
 */
@Service
public class BaseCategoryViewServiceImpl extends ServiceImpl<BaseCategoryViewMapper, BaseCategoryView> implements BaseCategoryViewService {

    /**
     * 查询首页三级分类信息
     * @return
     */
    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> retJsonObject = new ArrayList<>();
        List<BaseCategoryView> baseCategoryViews = baseMapper.selectList(null);
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));

        // 取一级分类信息
        Long index = 0L;
        for (Map.Entry<Long, List<BaseCategoryView>> longListEntry : category1Map.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            List<BaseCategoryView> value = longListEntry.getValue();
            BaseCategoryView baseCategoryView = value.get(0);
            jsonObject.put("categoryName", baseCategoryView.getCategory1Name());
            jsonObject.put("categoryId", baseCategoryView.getCategory1Id());
            jsonObject.put("index", ++index);

            // 取二级分类信息
            Map<Long, List<BaseCategoryView>> category2Group = value.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            List<JSONObject> category2JsonObjects = new ArrayList<>();
            for (Map.Entry<Long, List<BaseCategoryView>> category2Entry : category2Group.entrySet()) {
                JSONObject category2JsonObject = new JSONObject();
                List<BaseCategoryView> category2Value = category2Entry.getValue();
                BaseCategoryView category2BaseCategoryView = category2Value.get(0);
                category2JsonObject.put("categoryName", category2BaseCategoryView.getCategory2Name());
                category2JsonObject.put("categoryId", category2BaseCategoryView.getCategory2Id());

                // 取三级分类信息
                Map<Long, List<BaseCategoryView>> category3Group = value.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
                List<JSONObject> category3JsonObjects = new ArrayList<>();
                for (Map.Entry<Long, List<BaseCategoryView>> category3Entry : category3Group.entrySet()) {
                    JSONObject category3JsonObject = new JSONObject();
                    List<BaseCategoryView> category3Value = category3Entry.getValue();
                    BaseCategoryView category3BaseCategoryView = category3Value.get(0);
                    category3JsonObject.put("categoryName", category3BaseCategoryView.getCategory3Name());
                    category3JsonObject.put("categoryId", category3BaseCategoryView.getCategory3Id());
                    category3JsonObjects.add(category3JsonObject);
                    category2JsonObject.put("categoryChild", category3JsonObjects);
                }

                category2JsonObjects.add(category2JsonObject);
            }

            jsonObject.put("categoryChild", category2JsonObjects);

            retJsonObject.add(jsonObject);
        }

        return retJsonObject;
    }
}
