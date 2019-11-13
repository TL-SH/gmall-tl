package com.atguigu.gmall.item.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author tanglei
 */
@Service
public class ItemService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallSmsClient gmallSmsClient;

    @Autowired
    private GmallWmsClient gmallWmsClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public ItemVO item(Long skuId) {
        ItemVO itemVO = new ItemVO();
        // 使用异步编排对商品详情页进行改造
        CompletableFuture<SkuInfoEntity> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //1.查询sku信息
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            BeanUtils.copyProperties(skuInfoEntity, itemVO);
            return skuInfoEntity;
        },threadPoolExecutor);

        //Long spuId = skuInfoEntity.getSkuId();
        CompletableFuture<Void> brandCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //2.品牌
            Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.queryBrandByID(skuInfoEntity.getBrandId());
            itemVO.setBrand(brandEntityResp.getData());
        },threadPoolExecutor);

        CompletableFuture<Void> categoryCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //3.分类
            Resp<CategoryEntity> categoryEntityResp = this.gmallPmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
            itemVO.setCategory(categoryEntityResp.getData());
        },threadPoolExecutor);

        CompletableFuture<Void> spuInfoCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //4.spu信息
            Resp<SpuInfoEntity> spuInfoEntityResp = this.gmallPmsClient.querySpuById(skuInfoEntity.getSpuId());
            itemVO.setSpuInfo(spuInfoEntityResp.getData());
        },threadPoolExecutor);

        CompletableFuture<Void> picsCompletableFuture = CompletableFuture.runAsync(() -> {
            //5.设置图片信息
            Resp<List<String>> pics = this.gmallPmsClient.queryPicsBySkuId(skuId);
            itemVO.setPics(pics.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> itemSaleVoCompletableFuture = CompletableFuture.runAsync(() -> {
            //6.营销信息
            Resp<List<ItemSaleVO>> itemSaleVOsResp = this.gmallSmsClient.queryItemSaleVOs(skuId);
            itemVO.setSales(itemSaleVOsResp.getData());
        }, threadPoolExecutor);

        CompletableFuture<Void> wareCompletableFuture = CompletableFuture.runAsync(() -> {
            //7.是否有货
            Resp<List<WareSkuEntity>> queryWareResp = this.gmallWmsClient.queryWareBySkuId(skuId);
            List<WareSkuEntity> wareSkuEntities = queryWareResp.getData();
            itemVO.setStore(wareSkuEntities.stream().anyMatch(t -> t.getStock() > 0));
        }, threadPoolExecutor);

        CompletableFuture<Void> skuSaleCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //8.spu所有的销售属性
            Resp<List<SkuSaleAttrValueEntity>> skuSaleAttrValueResp = this.gmallPmsClient.querySaleAttrValues(skuInfoEntity.getSpuId());
            itemVO.setSkuSales(skuSaleAttrValueResp.getData());
        }, threadPoolExecutor);

        CompletableFuture<Void> spuInfoDescCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //9.spu的描述信息
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.gmallPmsClient.querySpuDescById(skuInfoEntity.getSpuId());
            itemVO.setDesc(spuInfoDescEntityResp.getData());
        }, threadPoolExecutor);

        CompletableFuture<Void> groupCompletableFuture = skuCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //10.规格属性分组及组下的规格参数设置
            Resp<List<GroupVO>> listResp = this.gmallPmsClient.queryGroupVOByCid(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId());
            itemVO.setGroups(listResp.getData());
        }, threadPoolExecutor);

        //等待所有线程执行完在执行
        CompletableFuture.allOf(brandCompletableFuture,categoryCompletableFuture,
                spuInfoCompletableFuture,picsCompletableFuture,
                itemSaleVoCompletableFuture,wareCompletableFuture,
                skuSaleCompletableFuture,spuInfoDescCompletableFuture,groupCompletableFuture).join();

        return itemVO;
    }

}
