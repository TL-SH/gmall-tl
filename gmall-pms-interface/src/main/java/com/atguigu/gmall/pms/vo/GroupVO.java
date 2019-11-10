package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import lombok.Data;

import java.util.List;

/**
 * @author tanglei
 */
@Data
public class GroupVO {
    // 分组名字 如 主体 基本信息
    private String groupName;
    // 基本属性
    private List<ProductAttrValueEntity> baseAttrValues;

}

