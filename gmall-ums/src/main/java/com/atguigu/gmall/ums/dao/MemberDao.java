package com.atguigu.gmall.ums.dao;

import com.atguigu.gmall.ums.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 会员
 * 
 * @author leishuai
 * @email 2258010965@qq.com
 * @date 2019-10-28 20:32:25
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {

    int updateIntegrationAndGrowth(@Param("userId") Long userId, @Param("grouth") Integer grouth, @Param("integration") Integer integration);
}
