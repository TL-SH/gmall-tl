package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 会员
 *
 * @author leishuai
 * @email 2258010965@qq.com
 * @date 2019-10-28 20:32:25
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);

    /**
     * 验证用户名是否存在
     * @param data
     * @param type
     * @return
     */
    Boolean check(String data, Integer type);

    /**
     * 注册
     * @param memberEntity
     * @param code
     */
    void register(MemberEntity memberEntity, String code);

    /**
     * 根据用户名密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    MemberEntity queryUser(String username, String password);
}

