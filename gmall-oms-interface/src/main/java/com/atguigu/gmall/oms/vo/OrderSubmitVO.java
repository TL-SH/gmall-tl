package com.atguigu.gmall.oms.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author tanglei
 */
@Data
public class OrderSubmitVO {

    private Long userId;
    private String userName;

    // 收货地址
    private MemberReceiveAddressEntity address;
    // 支付方式
    private Integer payType;
    // 配送公司(配送方式)
    private String deliveryCompany;
    //订单详情
    private List<OrderItemVO> orderItems;
    //下单时使用的积分
    private Integer useIntegration;
    //总价,用于验价
    private BigDecimal totalPrice;
    //防重,订单编号
    private String orderToken;



}
