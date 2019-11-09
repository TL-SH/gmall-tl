package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tanglei
 */
public interface GmallPmsApi {

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
