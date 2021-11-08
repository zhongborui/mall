package com.arui.mall.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.arui.mall.common.result.R;
import com.arui.mall.feign.client.ProductFeignClient;
import com.arui.mall.feign.client.SearchFeignClient;
import com.arui.mall.model.search.SearchParam;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取首页分类数据
 * @author ...
 */
@Controller
public class IndexController {

    @Resource
    private ProductFeignClient productFeignClient;

    @Resource
    private SearchFeignClient searchFeignClient;

//    @GetMapping(value = {"/", "index.html", "index"})
//    public String index(Model model){
//        List<JSONObject> baseCategoryList = productFeignClient.getBaseCategoryList();
//        List<JSONObject> list = baseCategoryList;
//        model.addAttribute("list", list);
//        return "index/index";
//    }

    @GetMapping("/search.html")
    public String a(SearchParam searchParam, Model model){
        R<Map> r = searchFeignClient.searchProduct(searchParam);
        model.addAllAttributes(r.getData());

        // 设置前端需要的searchParam
        model.addAttribute("searchParam", searchParam);

        // 设置搜索路径url
        String urlParam = pageUrlParam(searchParam);
        model.addAttribute("urlParam", urlParam);

        // 获取品牌显示
        String brandName = pageBrandParam(searchParam.getBrandName());
        model.addAttribute("brandNameParam", brandName);

        // 设置平台属性显示
        List<Map<String, String>> propsList = pagePropsParam(searchParam.getProps());
        model.addAttribute("propsParamList",propsList);

        //4.设置排序信息显示
        Map<String, Object> map = pageSortInfoParam(searchParam.getOrder());
        model.addAttribute("orderMap",map);


        return "search/index";
    }

    /**
     * 设置排序信息显示
     * @param order
     * @return
     */
    private Map<String, Object> pageSortInfoParam(String order) {
        Map<String,Object> map = new HashMap<>();
        if (!StringUtils.isEmpty(order)){
            //order=1:asc拆分数据
            String[] orderParams = order.split(":");
            if (orderParams!=null && orderParams.length==2){
                map.put("type",orderParams[0]);
                map.put("sort",orderParams[1]);
            }
        }else {
            //给一个默认的排序规则
            map.put("type","1");
            map.put("sort","asc");
        }
        return map;
    }

    /**
     * 设置平台属性显示
     * @param props
     * @return
     */
    private List<Map<String, String>> pagePropsParam(String[] props) {
        List<Map<String,String>> list = new ArrayList<>();
        if (props!=null && props.length>0){
            for (String prop : props) {
                //prop = 23:4G:运行内存
                String[] propPrams = prop.split(":");
                if (propPrams!=null && propPrams.length==3){
                    HashMap<String, String> map = new HashMap<>();
                    map.put("propertyKeyId",propPrams[0]);
                    map.put("propertyValue",propPrams[1]);
                    map.put("propertyKey",propPrams[2]);
                    list.add(map);
                }
            }
        }
        return list;

    }


    /**
     * 获取品牌显示 brandName=2:三星
     * @param brandName
     * @return
     */
    private String pageBrandParam(String brandName) {
        if (!StringUtils.isEmpty(brandName)){
            String[] brandNameParams = brandName.split(":");
            if (brandNameParams!=null && brandNameParams.length==2){
                return "品牌："+ brandNameParams[1];
            }
        }
        return "";
    }

    /**
     * 设置搜索路径url
     * http://search.gmall.com/search.html?keyword=三星
     * &props=23:4G:运行内存&props=24:128G:机身内存
     * @param searchParam
     * @return
     */
    private String pageUrlParam(SearchParam searchParam) {
        StringBuilder strParam = new StringBuilder("search.html?");
        // 三级分类id
        Long category3Id = searchParam.getCategory3Id();
        if (category3Id != null){
            strParam.append("category3Id=").append(category3Id);
        }
        // 搜索关键字
        String keyword = searchParam.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            strParam.append("keyword=").append(keyword);
        }

        // 品牌
        String brandName = searchParam.getBrandName();
        if (!StringUtils.isEmpty(brandName)) {
            if (strParam.length() > 0){
                strParam.append("&brandName=").append(brandName);
            }
        }

        // 平台属性多个 4:苹果A14:CPU型号
        String[] props = searchParam.getProps();
        if (!ArrayUtils.isEmpty(props)) {
            if (strParam.length() > 0){
                for (String prop : props) {
                    strParam.append("&props=").append(prop);
                }
            }
        }
        return strParam.toString();
    }

}
