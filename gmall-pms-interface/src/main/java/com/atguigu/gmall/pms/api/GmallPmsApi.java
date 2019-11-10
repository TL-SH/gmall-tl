package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tanglei
 */
public interface GmallPmsApi {


    /**
     * 根据skuId查询skuInfoEntity
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> querySkuById(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuId查询spuInfoEntity
     * @param id
     * @return
     */
    @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> querySpuById(@PathVariable("id") Long id);

    /**
     * 根据skuId查询skuImagesEntity的图片地址
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuimages/{skuId}")
    public Resp<List<String>> queryPicsBySkuId(@PathVariable("skuId")Long skuId);

    /**
     * 根据spuId查询销售属性信息
     * @param spuId
     * @return
     */
    @GetMapping("pms/skusaleattrvalue/{spuId}")
    public Resp<List<SkuSaleAttrValueEntity>> querySaleAttrValues(@PathVariable("spuId")Long spuId);

    /**
     * 根据spuId查询SpuInfoDescEntity
     * @param spuId
     * @return
     */
    @GetMapping("pms/spuinfodesc/info/{spuId}")
    public Resp<SpuInfoDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);


    /**
     * 根据三级分类id和spuId查询分组的基本信息
     * @param cid
     * @param spuId
     * @return
     */
    @GetMapping("pms/attrgroup/item/group/{cid}/{spuId}")
    public Resp<List<GroupVO>> queryGroupVOByCid(@PathVariable("cid")Long cid, @PathVariable("spuId")Long spuId);






    /**
     * 分页查询SpuInfoEntity
     * @param queryCondition
     * @return
     */
    @PostMapping("pms/spuinfo/list")
    public Resp<List<SpuInfoEntity>> querySpuPage(@RequestBody QueryCondition queryCondition);


    /**
     * 根据spuId查询skuInfo信息
     * @param spuId
     * @return
     */
    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuBySpuId(@PathVariable(value = "spuId")Long spuId);

    /**
     * 根据brandId(品牌id)查询BrandEntity(品牌数据)
     * @param brandId
     * @return
     */
    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> queryBrandByID(@PathVariable("brandId") Long brandId);


    /**
     * 根据catId(分类id)查询分类数据
     * @param catId
     * @return
     */
    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> queryCategoryById(@PathVariable("catId") Long catId);

    /**
     * 根据分类等级或者父类的id来查询
     * @param level
     * @param parentCid
     * @return
     */
    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> queryCategory(
            @RequestParam(value = "level",required = false,defaultValue = "0")Integer level,
            @RequestParam(value = "parentCid",required = false)Long parentCid );


    /**
     * 父类id查询二级分类以及子类
     * @param pid
     * @return
     */
    @GetMapping("pms/category/{pid}")
    public Resp<List<CategoryVO>> querySubCategory(@PathVariable("pid") Long pid);


    /**
     * 更据spuId查询需要搜索的规格参数
     * @param spuId
     * @return
     */
    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<SpuAttributeValueVO>> querySearchAttrValue(@PathVariable("spuId")Long spuId);


}
