package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.CategoryVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author leishuai
 * @email lxf@atguigu.com
 * @date 2019-10-28 20:21:21
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 查询所有分类
     * @param level
     * @param parentCid
     * @return
     */
    List<CategoryEntity> queryCategory(Integer level, Long parentCid);

    /**
     * 父类id查询二级分类以及子类
     * @param pid
     * @return
     */
    List<CategoryVO> querySubCategory(Long pid);
}

