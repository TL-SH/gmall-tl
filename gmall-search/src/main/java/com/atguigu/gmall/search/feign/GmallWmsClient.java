package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author tanglei
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {

}
