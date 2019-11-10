package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.sms.feigin.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author tanglei
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
