package com.atguigu.gmall.order.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.util.List;

/**
 * @author tanglei
 */
@Data
public class OrderConfirmVO {

    //收货地址
    private List<MemberReceiveAddressEntity> addresses;

    // 送货的清单
    private List<OrderItemVO> orderItems;

    // 积分信息
    private Integer bounds;

    // 防止订单重复提交
    private String orderToken;




}
