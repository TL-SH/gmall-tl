package com.atguigu.gmall.ums.feign;

import com.atguigu.core.bean.Resp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author tanglei
 */
@FeignClient("mms-service")
public interface GmallMmsClient {

    @GetMapping("mms/sendSms")
    public Resp<Object> sendSms(@RequestParam("phoneNum")String phoneNum);

}
