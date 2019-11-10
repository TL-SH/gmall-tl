package com.atguigu.gmall.sms.feigin;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.sms.vo.SaleVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author tanglei
 */
public interface GmallSmsApi {
    /**
     * 保存营销信息
     * @param saleVO
     * @return
     */
    @PostMapping("sms/skubounds/sale")
    Resp<Object> saveSale(@RequestBody SaleVO saleVO);

    /**
     * 根据skuId查询营销信息
     * @param skuId
     * @return
     */
    @GetMapping("sms/skubounds/item/sales/{skuId}")
    public Resp<List<ItemSaleVO>> queryItemSaleVOs(@PathVariable("skuId")Long skuId);

}
