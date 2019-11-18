package com.atguigu.gmall.order.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.cart.vo.CartItemVO;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderItemVO;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.feign.*;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.vo.SkuLockVO;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author tanglei
 */
@Service
public class OrderService {

    @Autowired
    private GmallUmsFeign gmallUmsClient;
    @Autowired
    private GmallPmsFeign gmallPmsClient;
    @Autowired
    private GmallSmsFeign gmallSmsClient;
    @Autowired
    private GmallWmsFeign gmallWmsClient;

    @Autowired
    private GmallOmsClient gmallOmsClient;

    @Autowired
    private GmallCartClient gmallCartClient;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;


    public static final String TOKEN_PREFIX = "order:token:";



    public OrderConfirmVO confirm() {
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();

        //获取用户登录的信息
        UserInfo userInfo = LoginInterceptor.get();

        //查询用户的收货地址的信息
        CompletableFuture<Void> addressCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<MemberReceiveAddressEntity>> addressResp = this.gmallUmsClient.queryAddressByUserId(userInfo.getUserId());
            orderConfirmVO.setAddresses(addressResp.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> itemCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //获取购物车中选中的记录
            Resp<List<CartItemVO>> cartItemVOResp = this.gmallCartClient.queryCartItemVO(userInfo.getUserId());
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
                Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(cartItemVO.getSkuId());
                SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
                //根据skuId查询销售属性
                Resp<List<SkuSaleAttrValueEntity>> skuSaleResp = this.gmallPmsClient.querySaleAttrBySkuId(cartItemVO.getSkuId());
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = skuSaleResp.getData();

                //根据skuId查询营销信息
                Resp<List<ItemSaleVO>> itemSaleResp = this.gmallSmsClient.queryItemSaleVOs(cartItemVO.getSkuId());
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
                Resp<List<WareSkuEntity>> storeResp = this.gmallWmsClient.queryWareBySkuId(cartItemVO.getSkuId());
                List<WareSkuEntity> wareSkuEntities = storeResp.getData();

                orderItemVO.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));

                return orderItemVO;
            }).collect(Collectors.toList());
            orderConfirmVO.setOrderItems(orderItemVOS);
        }, threadPoolExecutor);

        CompletableFuture<Void> boundsCompletableFuture = CompletableFuture.runAsync(() -> {
            //获取用户的信息(积分)
            Resp<MemberEntity> memberResp = this.gmallUmsClient.queryUserById(userInfo.getUserId());
            MemberEntity memberEntity = memberResp.getData();
            orderConfirmVO.setBounds(memberEntity.getIntegration());
        }, threadPoolExecutor);

        CompletableFuture<Void> tokenCompletableFuture = CompletableFuture.runAsync(() -> {
            //生成唯一的标志,防止重复提交
            String timeId = IdWorker.getTimeId();
            orderConfirmVO.setOrderToken(timeId);
            this.redisTemplate.opsForValue().set(TOKEN_PREFIX+timeId,timeId);
        }, threadPoolExecutor);

        CompletableFuture.allOf(addressCompletableFuture,itemCompletableFuture,
                boundsCompletableFuture,tokenCompletableFuture).join();

        return orderConfirmVO;
    }

    public void submit(OrderSubmitVO orderSubmitVO) {
        //1.验证令牌防止重复提交
        String orderToken = orderSubmitVO.getOrderToken();
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long flag = this.redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(TOKEN_PREFIX + orderToken), orderToken);
        if(flag==0l){
            throw new RuntimeException("请不要重复提交!!!!");
        }
        //2.验证价格
        BigDecimal totalPrice = orderSubmitVO.getTotalPrice();
        List<OrderItemVO> orderItems = orderSubmitVO.getOrderItems();
        if(CollectionUtils.isEmpty(orderItems)){
            throw new RuntimeException("请添加购物车清单!");
        }
        BigDecimal currentPrice = orderItems.stream().map(orderItemVO -> {
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(orderItemVO.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            return skuInfoEntity.getPrice().multiply(new BigDecimal(orderItemVO.getCount()));
        }).reduce((a, b) -> a.add(b)).get();

        if(totalPrice.compareTo(currentPrice)!=0){
            throw new RuntimeException("请刷新页面后重试");
        }
        //3. 验证库存，并锁定库存
        List<SkuLockVO> skuLockVOS = orderItems.stream().map(orderItemVO -> {
            SkuLockVO skuLockVO = new SkuLockVO();
            skuLockVO.setSkuId(orderItemVO.getSkuId());
            skuLockVO.setCount(orderItemVO.getCount());
            skuLockVO.setOrderToken(orderToken);
            return skuLockVO;
        }).collect(Collectors.toList());

        Resp<Object> objectResp = this.gmallWmsClient.checkAndLock(skuLockVOS);
        if (objectResp.getCode() == 1) {
            throw new RuntimeException(objectResp.getMsg());
        }
        //4.生成订单
        UserInfo userInfo = LoginInterceptor.get();
        try {
            orderSubmitVO.setUserId(userInfo.getUserId());
            Resp<MemberEntity> memberEntityResp = this.gmallUmsClient.queryUserById(userInfo.getUserId());
            MemberEntity memberEntity = memberEntityResp.getData();
            orderSubmitVO.setUserName(memberEntity.getUsername());
            Resp<OrderEntity> orderResp = this.gmallOmsClient.createOrder(orderSubmitVO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("订单创建失败,服务器异常!");
        }

        //5.删除购物车中对应的记录(消息队列)
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userInfo.getUserId());
        List<Long> skuIds = orderItems.stream().map(orderItemVO -> orderItemVO.getSkuId()).collect(Collectors.toList());
        map.put("skuIds",skuIds);
        this.amqpTemplate.convertAndSend("GMALL-ORDER-EXCHANGE","cart.delete",map);

    }
}
