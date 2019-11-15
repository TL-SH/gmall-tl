package com.atguigu.gmall.order.feign;

import com.atguigu.gmall.sms.feigin.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author tanglei
 */
@FeignClient("sms-service")
public interface GmallSmsFeign extends GmallSmsApi {
}
