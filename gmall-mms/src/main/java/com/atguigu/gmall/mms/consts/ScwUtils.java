package com.atguigu.gmall.mms.consts;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author tanglei
 * @date 2019/9/23  19:14
 */
public class ScwUtils {

    //保存数据到redis中
    public static<T> void saveBeanRedis(T t,StringRedisTemplate stringRedisTemplate,String key,String prefix){
        String jsonString = JSON.toJSONString(t);
        stringRedisTemplate.opsForValue().set(prefix+key,jsonString);
    }


    //封装从reids中读取字符串并封装成基本数据类型的方法
    public static<T> T getBeanFromJson(StringRedisTemplate stringRedisTemplate,String key,Class<T> type){
        //先查询json字符串
        String jsonStr = stringRedisTemplate.opsForValue().get(key);
        //再将json字符串转化为指定的数据类型
        if(StringUtils.isEmpty(jsonStr)){
            return null;
        }
        T t = JSON.parseObject(jsonStr,type);
        return t;
    }



    //验证手机格式的方法
    public static boolean isMobilePhone(String phone) {
        boolean flag=true;
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            flag= false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            flag = m.matches();
        }

        return flag;
    }
}

