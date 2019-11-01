package com.atguigu.gmall.sms.feigin;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SaleVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author tanglei
 */
public interface GmallSmsApi {

    @PostMapping("sms/skubounds/sale")
    Resp<Object> saveSale(@RequestBody SaleVO saleVO);

}
