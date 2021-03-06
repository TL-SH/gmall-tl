package com.atguigu.gmall.ums.api;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author tanglei
 */
public interface GmallUmsApi {

    /**
     * 根据用户id查询用户信息
     * @param id
     * @return
     */
    @GetMapping("ums/member/info/{id}")
    public Resp<MemberEntity> queryUserById(@PathVariable("id") Long id);

    /**
     * 根据userId查询地址信息
     * @param userId
     * @return
     */
    @GetMapping("ums/memberreceiveaddress/{userId}")
    public Resp<List<MemberReceiveAddressEntity>> queryAddressByUserId(@PathVariable("userId")Long userId);

    /**
     * 根据用户名和密码查询用户的信息
     * @param username
     * @param password
     * @return
     */
    @GetMapping("ums/member/query")
    public Resp<MemberEntity> queryUser(@RequestParam("username")String username, @RequestParam("password")String password);

}
