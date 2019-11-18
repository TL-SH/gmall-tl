package com.atguigu.gmall.wms.vo;

import lombok.Data;

/**
 * @author tanglei
 */
@Data
public class SkuLockVO {

    private Long skuId;
    private Integer count;
    //锁定成功为true 锁定失败为false
    private Boolean lock;
    //锁定库存的id
    private Long skuWareId;

    private String orderToken;

}
