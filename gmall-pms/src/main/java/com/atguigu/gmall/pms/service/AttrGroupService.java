package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.GroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author leishuai
 * @date 2019-10-28 20:21:21
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 根据三级分类的id查询分页
     * @param catId
     * @param condition
     * @return
     */
    PageVo queryByCidPage(Long catId, QueryCondition condition);

    /**
     * 根据分组id查询分组以下的规格数据
     * @param gid
     * @return
     */
    AttrGroupVO queryById(Long gid);

    /**
     * 根据三级分类查询分组及组下的规格数据
     * @param catId
     * @return
     */
    List<AttrGroupVO> queryGroupWithAttrsByCid(Long catId);

    /**
     * 根据三级分类和spuId查询group
     * @param cid
     * @param spuId
     * @return
     */
    List<GroupVO> queryGroupVOByCid(Long cid, Long spuId);
}

