package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author tanglei
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
