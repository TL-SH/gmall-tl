package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
     * 更据spuId查询需要搜索的规格参数
     * @param spuId
     * @return
     */
    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<SpuAttributeValueVO>> querySearchAttrValue(@PathVariable("spuId")Long spuId);


}
