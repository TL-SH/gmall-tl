package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author tanglei
 */
public class ProductAttrValueVO extends ProductAttrValueEntity {

    public void setValueSelected(List<String> valueSelected){
        // 如果接受的集合为空，则不设置
        if (CollectionUtils.isEmpty(valueSelected)){
            return;
        }
        this.setAttrValue(StringUtils.join(valueSelected, ","));
    }
}
