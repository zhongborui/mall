package com.arui.mall.model.search;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索返回结果
 * @author ...
 */
// 总的数据
@Data
public class SearchResponseVO implements Serializable {

    //品牌 此时vo对象中的id字段保留（不用写） name就是“品牌” value: [{id:100,name:华为,logo:xxx},{id:101,name:小米,log:yyy}]
    private List<SearchBrandVo> brandVoList;
    //所有商品的顶头显示的筛选属性
    private List<SearchPlatformPropertyVo> platformPropertyList = new ArrayList<>();

    //检索出来的商品信息
    private List<Product> productList = new ArrayList<>();

    private Long total;//总记录数
    private Integer pageSize;//每页显示的内容
    private Integer pageNo;//当前页面
    private Long totalPages;

}
