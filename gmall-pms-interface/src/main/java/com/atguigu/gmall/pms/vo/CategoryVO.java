package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import lombok.Data;

import java.util.List;

/**
 * @author tanglei
 */
@Data
public class CategoryVO extends CategoryEntity{
    private List<CategoryEntity> subs;
}
