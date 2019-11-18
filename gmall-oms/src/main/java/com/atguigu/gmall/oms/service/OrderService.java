package com.atguigu.gmall.oms.service;

import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 订单
 *
 * @author leishuai
 * @email lxf@atguigu.com
 * @date 2019-10-28 20:26:07
 */
public interface OrderService extends IService<OrderEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 创建订单
     * @param orderSubmitVO
     * @return
     */
    OrderEntity createOrder(OrderSubmitVO orderSubmitVO);

    /**
     * 修改订单的状态
     * @param orderToken
     * @return
     */
    int closeOrder(String orderToken);

    int success(String orderToken);
}

