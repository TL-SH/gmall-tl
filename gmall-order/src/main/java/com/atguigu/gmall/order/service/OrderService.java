package com.atguigu.gmall.order.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.cart.api.GmallCartApi;
import com.atguigu.gmall.cart.vo.CartItemVO;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.OrderItemVO;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.feigin.GmallSmsApi;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.ums.api.GmallUmsApi;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.api.GmallWmsApi;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author tanglei
 */
@Service
public class OrderService {

    @Autowired
    private GmallCartApi gmallCartApi;

    @Autowired
    private GmallWmsApi gmallWmsApi;

    @Autowired
    private GmallSmsApi gmallSmsApi;

    @Autowired
    private GmallPmsApi gmallPmsApi;

    @Autowired
    private GmallUmsApi gmallUmsApi;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    public OrderConfirmVO confirm() {
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();

        //获取用户登录的信息
        UserInfo userInfo = LoginInterceptor.get();

        //查询用户的收货地址的信息
        CompletableFuture<Void> addressCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<MemberReceiveAddressEntity>> addressResp = this.gmallUmsApi.queryAddressByUserId(userInfo.getUserId());
            orderConfirmVO.setAddresses(addressResp.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> itemCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //获取购物车中选中的记录
            Resp<List<CartItemVO>> cartItemVOResp = this.gmallCartApi.queryCartItemVO(userInfo.getUserId());
            List<CartItemVO> itemVOS = cartItemVOResp.getData();
            return itemVOS;
        }, threadPoolExecutor).thenAcceptAsync(itemVOS -> {
            if (CollectionUtils.isEmpty(itemVOS)) {
                return;
            }
            //把购物车所选中记录转化成订货清单
            List<OrderItemVO> orderItemVOS = itemVOS.stream().map(cartItemVO -> {
                OrderItemVO orderItemVO = new OrderItemVO();
                //根据skuId查询sku的信息
                Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsApi.querySkuById(cartItemVO.getSkuId());
                SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                //根据skuId查询销售属性
                Resp<List<SkuSaleAttrValueEntity>> skuSaleResp = this.gmallPmsApi.querySaleAttrBySkuId(cartItemVO.getSkuId());
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuSaleResp.getData();

                //根据skuId查询营销信息
                Resp<List<ItemSaleVO>> itemSaleResp = this.gmallSmsApi.queryItemSaleVOs(cartItemVO.getSkuId());
                List<ItemSaleVO> itemSaleVOS = itemSaleResp.getData();

                orderItemVO.setSkuId(cartItemVO.getSkuId());
                orderItemVO.setTitle(skuInfoEntity.getSkuTitle());
                orderItemVO.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
                orderItemVO.setSales(itemSaleVOS);
                orderItemVO.setSkuAttrValue(skuSaleAttrValueEntities);
                orderItemVO.setPrice(skuInfoEntity.getPrice());
                orderItemVO.setCount(cartItemVO.getCount());
                orderItemVO.setWeight(skuInfoEntity.getWeight());
                // 根据skuI查询库存信息
                Resp<List<WareSkuEntity>> storeResp = this.gmallWmsApi.queryWareBySkuId(cartItemVO.getSkuId());
                List<WareSkuEntity> wareSkuEntities = storeResp.getData();

                orderItemVO.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));

                return orderItemVO;
            }).collect(Collectors.toList());
            orderConfirmVO.setOrderItems(orderItemVOS);
        }, threadPoolExecutor);

        CompletableFuture<Void> boundsCompletableFuture = CompletableFuture.runAsync(() -> {
            //获取用户的信息(积分)
            Resp<MemberEntity> memberResp = this.gmallUmsApi.info(userInfo.getUserId());
            MemberEntity memberEntity = memberResp.getData();
            orderConfirmVO.setBounds(memberEntity.getIntegration());
        }, threadPoolExecutor);

        CompletableFuture<Void> tokenCompletableFuture = CompletableFuture.runAsync(() -> {
            //生成唯一的标志,防止重复提交
            String timeId = IdWorker.getTimeId();
            orderConfirmVO.setOrderToken(timeId);
        }, threadPoolExecutor);

        CompletableFuture.allOf(addressCompletableFuture,itemCompletableFuture,
                boundsCompletableFuture,tokenCompletableFuture).join();

        return orderConfirmVO;
    }
}
