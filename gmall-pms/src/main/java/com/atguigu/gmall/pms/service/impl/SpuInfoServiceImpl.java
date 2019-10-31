package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.vo.ProductAttrValueVO;
import com.atguigu.gmall.pms.vo.SaleVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao spuInfoDescDao;

    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private SkuImagesDao skuImagesDao;

    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Autowired
    private GmallSmsClient smsClient;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfoByKeyPage(Long catId, QueryCondition condition) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        //判断cid是否为空
        if (catId != 0) {
            wrapper.eq("catelog_id",catId);
        }

        //判断key是否为空
        String key = condition.getKey();
        if(StringUtils.isNotBlank(key)){
            wrapper.and(t->t.eq("id",key).or().like("spu_name",key));
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                wrapper
        );
        return new PageVo(page);
    }

    @Override
    public void bigSave(SpuInfoVO spuInfoVO) {
        //1.新增spu三张表
        //1.1   新增spuInfo
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
        this.save(spuInfoVO);
        // 获取spuId
        Long spuId = spuInfoVO.getId();
        //1.2   新增spuInfoDesc
        List<String> spuImages = spuInfoVO.getSpuImages();
        String desc = StringUtils.join(spuImages, ",");
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(desc);
        this.spuInfoDescDao.insert(spuInfoDescEntity);

        //1.3   新增基本属性productAttrValue
        List<ProductAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        baseAttrs.forEach(baseAttr -> {
            baseAttr.setSpuId(spuId);
            baseAttr.setAttrSort(0);
            baseAttr.setQuickShow(1);
            this.productAttrValueDao.insert(baseAttr);
        });

        //2.新增sku相关的三张表 必须得有spuId

        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if(CollectionUtils.isEmpty(skus)){
            return;
        }
        skus.forEach(skuInfoVO -> {
            //2.1   新增skuInfo基本信息

            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO,skuInfoEntity);
            skuInfoEntity.setBrandId(skuInfoVO.getBrandId());
            skuInfoEntity.setCatalogId(skuInfoVO.getCatalogId());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            skuInfoEntity.setSpuId(spuId);
            List<String> images = skuInfoVO.getImages();
            //设置默认图片
            if(!CollectionUtils.isEmpty(images)){
                skuInfoEntity.setSkuDefaultImg(StringUtils.isNotBlank(skuInfoEntity.getSkuDefaultImg())?skuInfoEntity.getSkuDefaultImg():images.get(0));
            }
            this.skuInfoDao.insert(skuInfoEntity);
            //获取skuId
            Long skuId = skuInfoEntity.getSkuId();
            //2.2   新增sku的图片信息
            if(!CollectionUtils.isEmpty(images)){
                images.forEach(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgSort(0);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(image,skuInfoEntity.getSkuDefaultImg())?1:0);
                    skuImagesEntity.setImgUrl(image);
                    this.skuImagesDao.insert(skuImagesEntity);
                });

            }
            //2.3   新增sku的规格参数(销售属性)
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            if(!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(saleAttr ->{
                    saleAttr.setSkuId(skuId);
                    saleAttr.setAttrSort(0);
                    this.skuSaleAttrValueDao.insert(saleAttr);
                });
            }
            //3.新增营销相关信息的三张表,需要远程调用gmall-sms
            SaleVO saleVO = new SaleVO();
            BeanUtils.copyProperties(skuInfoVO,saleVO);
            saleVO.setSkuId(skuId);
            this.smsClient.saveSale(saleVO);

        });

    }

}