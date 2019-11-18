package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author leishuai
 * @email lxf@atguigu.com
 * @date 2019-10-28 20:26:07
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    /**
     * 关闭订单
     * @param orderToken
     * @return
     */
    int closeOrder(String orderToken);

    /**
     * 支付成功,修改订单的状态为待发货
     * @param orderToken
     * @return
     */
    int success(String orderToken);


}
