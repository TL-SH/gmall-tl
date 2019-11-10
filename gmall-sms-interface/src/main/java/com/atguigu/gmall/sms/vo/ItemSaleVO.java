package com.atguigu.gmall.sms.vo;

import lombok.Data;

/**
 * @author tanglei
 */
@Data
public class ItemSaleVO {

    // 满减 打折 积分
    private String type;

    // 优惠信息的具体描述
    private String desc;
}
