package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author tanglei
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {
}
