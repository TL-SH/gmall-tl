package com.atguigu.gmall.sms.service.impl;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {
    @Autowired
    private SkuFullReductionDao skuFullReductionDao;

    @Autowired
    private SkuLadderDao skuLadderDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    //@Transactional
    @Override
    public void saveSale(SaleVO saleVO) {
        //3.1  积分优惠 skuBounds
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        skuBoundsEntity.setBuyBounds(saleVO.getBuyBounds());
        skuBoundsEntity.setGrowBounds(saleVO.getGrowBounds());
        skuBoundsEntity.setSkuId(saleVO.getSkuId());
        List<Integer> works = saleVO.getWork();
        if(!CollectionUtils.isEmpty(works) && works.size()==4){
            skuBoundsEntity.setWork(works.get(3)*1+works.get(2)*2+works.get(1)*4+works.get(0)*8);
        }
        this.save(skuBoundsEntity);

        //3.2  数量折扣 skuLadder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setFullCount(saleVO.getFullCount());
        skuLadderEntity.setDiscount(saleVO.getDiscount());
        skuLadderEntity.setAddOther(saleVO.getLadderAddOther());
        skuLadderEntity.setSkuId(saleVO.getSkuId());
        this.skuLadderDao.insert(skuLadderEntity);

        //3.3  满减优惠 skuReduction
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setAddOther(saleVO.getFullAddOther());
        skuFullReductionEntity.setSkuId(saleVO.getSkuId());
        skuFullReductionEntity.setReducePrice(saleVO.getReducePrice());
        skuFullReductionEntity.setFullPrice(saleVO.getFullPrice());
        this.skuFullReductionDao.insert(skuFullReductionEntity);

    }

    @Override
    public List<ItemSaleVO> queryItemSaleVOs(Long skuId) {
        List<ItemSaleVO> itemSaleVOS = new ArrayList<>();

        //查询积分的信息
        List<SkuBoundsEntity> skuBoundsEntities = this.list(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if (!CollectionUtils.isEmpty(skuBoundsEntities)) {
            ItemSaleVO saleVO = new ItemSaleVO();
            saleVO.setType("积分");
            BigDecimal buyBounds = skuBoundsEntities.get(0).getBuyBounds();
            BigDecimal growBounds = skuBoundsEntities.get(0).getGrowBounds();
            saleVO.setDesc("购物积分赠送"+buyBounds.intValue()+",成长积分赠送"+growBounds.intValue());
            itemSaleVOS.add(saleVO);
        }

        //查询满减信息
        List<SkuFullReductionEntity> skuFullReductionEntities = this.skuFullReductionDao.selectList(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if (!CollectionUtils.isEmpty(skuFullReductionEntities)) {
            ItemSaleVO saleVO = new ItemSaleVO();
            saleVO.setType("满减");
            BigDecimal fullPrice = skuFullReductionEntities.get(0).getFullPrice();
            BigDecimal reducePrice = skuFullReductionEntities.get(0).getReducePrice();
            saleVO.setDesc("满"+fullPrice.intValue()+"减"+reducePrice.intValue());
            itemSaleVOS.add(saleVO);
        }

        //查询打折信息
        List<SkuLadderEntity> skuLadderEntities = this.skuLadderDao.selectList(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if (!CollectionUtils.isEmpty(skuLadderEntities)) {
            ItemSaleVO saleVO = new ItemSaleVO();
            saleVO.setType("打折");
            Integer fullCount = skuLadderEntities.get(0).getFullCount();
            BigDecimal discount = skuLadderEntities.get(0).getDiscount();
            saleVO.setDesc("满"+fullCount +"件打"+discount.divide(new BigDecimal(10)).floatValue()+"折");
            itemSaleVOS.add(saleVO);
        }

        return itemSaleVOS;
    }

}