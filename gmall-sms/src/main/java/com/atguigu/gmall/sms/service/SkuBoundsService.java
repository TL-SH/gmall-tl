package com.atguigu.gmall.sms.service;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.vo.SaleVO;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 商品sku积分设置
 *
 * @author leishuai
 * @email 2258010965@qq.com
 * @date 2019-10-28 20:29:24
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 新增sku的营销信息
     * @param saleVo
     */
    void saveSale(SaleVO saleVo);

}

