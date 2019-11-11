package com.atguigu.gmall.ums.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;

import java.util.Date;
import java.util.UUID;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean check(String data, Integer type) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type){
            case 1 : wrapper.eq("username",type); break;
            case 2 : wrapper.eq("mobile",type); break;
            case 3 : wrapper.eq("email",type); break;
            default:return false;
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {
        //1.校验短信验证码

        //2.生成盐
        String salt = StringUtils.substring(UUID.randomUUID().toString(), 0, 6);
        memberEntity.setSalt(salt);
        //3.加盐加密
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword()+salt));
        //4.写入数据库
        memberEntity.setStatus(1);
        memberEntity.setLevelId(1l);
        memberEntity.setCreateTime(new Date());
        memberEntity.setIntegration(0);
        this.save(memberEntity);

        //5.删除Redis中的验证码

    }

    @Override
    public MemberEntity queryUser(String username, String password) {
        //查询
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("username", username));
        //效验用户名
        if (memberEntity == null) {
            throw new IllegalArgumentException("用户名或密码不合法,请重新输入!");
        }
        //对用户数据库密码进行加密
        password = DigestUtils.md5Hex(password+memberEntity.getSalt());

        //校验密码
        if(!StringUtils.equals(password,memberEntity.getPassword())){
            throw new IllegalArgumentException("用户名或密码不合法,请重新输入!");
        }
        return memberEntity;
    }




}