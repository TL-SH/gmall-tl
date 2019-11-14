package com.atguigu.gmall.mms.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.mms.consts.AppConsts;
import com.atguigu.gmall.mms.consts.ScwUtils;
import com.atguigu.gmall.mms.template.SmsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author tanglei
 */
@Slf4j
@RestController
@RequestMapping("mms")
public class SmsController {

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("/sendSms")
    public Resp<Object> sendSms(@RequestParam("phoneNum")String phoneNum){

        //验证手机号码格式
        boolean b = ScwUtils.isMobilePhone(phoneNum);
        if(!b){
            return Resp.fail("手机号码格式错误");
        }
        //验证redis中存储的当前手机号码获取验证码的此次(第一次获取没有或者没有超过指定次数可以继续获取验证码)
        //手机号码最多5次  格式 code:18726009750:count
        String countStr= stringRedisTemplate.opsForValue().get(AppConsts.CODE_PREFIX + phoneNum + AppConsts.CODE_COUNT_SUFFIX);
        int count=0;
        if(!StringUtils.isEmpty(countStr)){
            //如果数量字符串,不为空,转为数字
            count = Integer.parseInt(countStr);
        }
        if(count>=5){
            return Resp.fail("验证码次数超出范围");
        }

        //验证手机验证码是否存在未过期的验证码
        //获取手机在当前redis中是否有过期的验证码:如果为空,则代表没有
        Boolean key = stringRedisTemplate.hasKey(AppConsts.CODE_PREFIX + phoneNum + AppConsts.CODE_CODE_SUFFIX);
        if(key){
            return Resp.fail("请勿频繁操作验证码");
        }

        //发送验证码
        //随机生成6位数验证码
        String code = UUID.randomUUID().toString().replace("-","").substring(0,6);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phoneNum);
        querys.put("param", AppConsts.CODE_PREFIX+code);
        querys.put("tpl_id", "TP1711063");
        Boolean sendSms = smsTemplate.sendSms(querys);
        if(!sendSms){
            return Resp.fail("短信发送失败");
        }
        //将验证码存入redis数据库5分钟
        stringRedisTemplate.opsForValue().set(AppConsts.CODE_PREFIX+phoneNum+AppConsts.CODE_CODE_SUFFIX, code, 5, TimeUnit.MINUTES);
        //修改该手机号码发送验证码的次数
        Long expire = stringRedisTemplate.getExpire(AppConsts.CODE_PREFIX+phoneNum+AppConsts.CODE_COUNT_SUFFIX , TimeUnit.MINUTES);//获取次数的过期时间
        log.info("查询为过期的事件:{}", expire);// -2代表已過期(未注册)
        if(expire==null  || expire<=0 ) {
            expire = (long) (24*60);
        }
        count++;
        stringRedisTemplate.opsForValue().set(AppConsts.CODE_PREFIX+phoneNum+AppConsts.CODE_COUNT_SUFFIX, count+"", expire, TimeUnit.MINUTES);
        //响应成功
        return Resp.ok("发送验证码成功");
    }

}
