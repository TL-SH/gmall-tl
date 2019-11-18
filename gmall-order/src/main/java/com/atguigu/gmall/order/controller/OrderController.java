package com.atguigu.gmall.order.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.oms.entity.OrderEntity;
import com.atguigu.gmall.oms.vo.OrderSubmitVO;
import com.atguigu.gmall.order.config.AlipayTemplate;
import com.atguigu.gmall.order.interceptor.LoginInterceptor;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.vo.OrderConfirmVO;
import com.atguigu.gmall.order.vo.PayAsyncVo;
import com.atguigu.gmall.order.vo.PayVo;
import com.atguigu.gmall.order.vo.SeckillVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author tanglei
 */
@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 确认提交
     * @return
     */
    @GetMapping("confirm")
    public Resp<OrderConfirmVO> confirm(){
        OrderConfirmVO orderConfirmVO  = this.orderService.confirm();
        return Resp.ok(orderConfirmVO);
    }

    /**
     * 提交订单
     * @param orderSubmitVO
     * @return
     */
    @PostMapping("submit")
    public Resp<Object> submit(@RequestBody OrderSubmitVO orderSubmitVO){
        String form = null;
        try {
            OrderEntity orderEntity = this.orderService.submit(orderSubmitVO);
            //封装支付vo
            PayVo payVo = new PayVo();
            payVo.setBody("谷粒商城支付平台");
            payVo.setSubject("支付平台");
            payVo.setOut_trade_no(orderEntity.getOrderSn());
            payVo.setTotal_amount(orderEntity.getTotalAmount().toString());
            form = this.alipayTemplate.pay(payVo);
            System.out.println(form);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Resp.ok(form);
    }

    /**
     * 支付
     * @return
     */
    @PostMapping("pay/success")
    public Resp<Object> paySuccess(PayAsyncVo payAsyncVo){

        System.out.println("------------------支付成功---------------------");
        //订单状态的修改和库存的扣除
        this.orderService.paySuccess(payAsyncVo.getOut_trade_no());
        return Resp.ok(null);
    }

    /**
     * 秒杀
     * @param skuId
     * @return
     */
    @RequestMapping("seckill/{skuId}")
    public Resp<Object> seckill(@PathVariable("skuId")Long skuId) throws InterruptedException {
        //查询秒杀库存
        String stockJson = this.redisTemplate.opsForValue().get("seckill:stock:" + skuId);
        if(StringUtils.isEmpty(stockJson)){
            return Resp.ok("该秒杀不存在");
        }
        // 转化为数字
        Integer stock = Integer.valueOf(stockJson);

        // 获取信号量锁
        RSemaphore semaphore = this.redissonClient.getSemaphore("seckill:lock:" + skuId);
        // 设置库存量
        semaphore.trySetPermits(stock);
        // 每次请求过来占用一个
        semaphore.acquire(1);

        UserInfo userInfo = LoginInterceptor.get();
        // 计时锁
        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("seckill:count:" + userInfo.getUserId());
        // 计数开始
        countDownLatch.trySetCount(1);

        SeckillVO seckillVO = new SeckillVO();
        seckillVO.setSkuId(skuId);
        seckillVO.setUserId(userInfo.getUserId());
        seckillVO.setCount(1);
        // 发送的消息创建订单
        this.amqpTemplate.convertAndSend("SECKILL-EXCHANGE","seckill.create",seckillVO);

        // 更新数量
        this.redisTemplate.opsForValue().set("seckill:stock"+skuId,String.valueOf(--stock));

        return Resp.ok(null);
    }

    @GetMapping
    public Resp<OrderEntity> queryOrder() throws InterruptedException {
        UserInfo userInfo = LoginInterceptor.get();
        RCountDownLatch countDownLatch = this.redissonClient.getCountDownLatch("seckill:count:" + userInfo.getUserId());
        countDownLatch.await();

        // 查询订单
        OrderEntity orderEntity = this.orderService.queryOrder();

        return Resp.ok(orderEntity);
    }

}