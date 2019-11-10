package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import lombok.Data;

import java.util.List;

/**
 * @author tanglei
 */
@Data
public class ItemVO extends SkuInfoEntity {
    // 品牌信息
    private BrandEntity brand;

    //分类信息
    private CategoryEntity category;

    //spuInfo信息
    private SpuInfoEntity spuInfo;

    //sku的图片列表
    private List<String> pics;

    //营销信息
    private List<ItemSaleVO> sales;

    //是否有货
    private Boolean store;

    //spu下所有sku的销售属性
    private List<SkuSaleAttrValueEntity> skuSales;

    //描述信息
    private SpuInfoDescEntity desc;

    //组及组下的规格属性及值
    private List<GroupVO> groups;


}
