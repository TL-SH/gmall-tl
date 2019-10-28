package com.atguigu.gmall.wms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.wms.entity.ShAreaEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 全国省市区信息
 *
 * @author leishuai
 * @email 2258010965@qq.com
 * @date 2019-10-28 20:35:03
 */
public interface ShAreaService extends IService<ShAreaEntity> {

    PageVo queryPage(QueryCondition params);
}

