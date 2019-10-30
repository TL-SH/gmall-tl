package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品属性
 *
 * @author leishuai
 * @email lxf@atguigu.com
 * @date 2019-10-28 20:21:21
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 根据条件查询分页信息
     * @param cid
     * @param type
     * @param condition
     * @return
     */
    PageVo queryByCidTypePage(Long cid, Integer type, QueryCondition condition);

    /**
     * 保存
     * @param attrVO
     */
    void saveAttrAndRelation(AttrVO attrVO);
}

