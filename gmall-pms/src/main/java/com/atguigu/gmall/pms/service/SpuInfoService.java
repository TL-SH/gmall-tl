package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu信息
 *
 * @author leishuai
 * @email lxf@atguigu.com
 * @date 2019-10-28 20:21:21
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 查询善品的列表
     * @param catId
     * @param condition
     * @return
     */
    PageVo querySpuInfoByKeyPage(Long catId, QueryCondition condition);

    /**
     * SPU新增功能
     * @param spuInfoVO
     */
    void bigSave(SpuInfoVO spuInfoVO);

}

