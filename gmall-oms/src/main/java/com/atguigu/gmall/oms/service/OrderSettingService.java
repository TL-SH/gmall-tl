package com.atguigu.gmall.oms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.oms.entity.OrderSettingEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 订单配置信息
 *
 * @author leishuai
 * @email lxf@atguigu.com
 * @date 2019-10-28 20:26:07
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageVo queryPage(QueryCondition params);
}

