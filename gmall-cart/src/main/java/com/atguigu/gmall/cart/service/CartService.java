package com.atguigu.gmall.cart.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.feign.GmallPmsClient;
import com.atguigu.gmall.cart.feign.GmallSmsClient;
import com.atguigu.gmall.cart.interceptor.LoginInterceptor;
import com.atguigu.gmall.cart.vo.Cart;
import com.atguigu.core.bean.UserInfo;
import com.atguigu.gmall.cart.vo.CartItemVO;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.sms.vo.ItemSaleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author tanglei
 */
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallSmsClient gmallSmsClient;

    public static final String KEY_PREFIX="cart:key:";

    public static final String CURRENT_PRICE_PREFIX="cart:price:";

    public void addCart(Cart cart) {
        String key = getKey();
        //判断购物车中是否有该记录
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        //取出用户新增购物车的数量
        Integer count = cart.getCount();
        String skuId = cart.getSkuId().toString();

        //有更新数量
        if(hashOps.hasKey(cart.getSkuId().toString())){
            String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
            cart = JSON.parseObject(cartJson, Cart.class);
            //跟新数量
            cart.setCount(cart.getCount()+count);
        }else {
            //没有新增记录 查询sku信息
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(cart.getSkuId());
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            cart.setTitle(skuInfoEntity.getSkuTitle());
            cart.setDefaultImage(skuInfoEntity.getSkuDefaultImg());
            cart.setPrice(skuInfoEntity.getPrice());
            cart.setCheck(true);
            //查询销售属性
            Resp<List<SkuSaleAttrValueEntity>> saleAttrResp = this.gmallPmsClient.querySaleAttrBySkuId(cart.getSkuId());
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = saleAttrResp.getData();
            cart.setSkuAttrValue(skuSaleAttrValueEntities);
            //查询营销信息
            Resp<List<ItemSaleVO>> listResp = this.gmallSmsClient.queryItemSaleVOs(cart.getSkuId());
            cart.setSales(listResp.getData());

            //将当前的价格同步到redis
            this.redisTemplate.opsForValue().set(CURRENT_PRICE_PREFIX+skuId,skuInfoEntity.getPrice().toString());

        }
        //同步到redis中
        hashOps.put(skuId,JSON.toJSONString(cart));
    }




    public List<Cart> queryCarts() {
        //查询未登录状态的购物车
        UserInfo userInfo = LoginInterceptor.get();
        String key1 = KEY_PREFIX + userInfo.getUserKey();
        
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key1);
        List<Object> cartJsonList = hashOps.values();
        List<Cart> userKeyCarts = null;
        if(!CollectionUtils.isEmpty(cartJsonList)){
            userKeyCarts = cartJsonList.stream().map(catJson-> {
                Cart cart = JSON.parseObject(catJson.toString(), Cart.class);

                //查询当前的价格
                cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX+cart.getSkuId())));
                return cart;
            }).collect(Collectors.toList());
        }
        // 判断登录状态
        if(userInfo.getUserId()==null){
            //未登录就直接返回
            return userKeyCarts;
        }

        // 登录,查询登录状态的购物车
        String key2 = KEY_PREFIX + userInfo.getUserId();
        BoundHashOperations<String, Object, Object> userIdOps = this.redisTemplate.boundHashOps(key2);
        // 判未登录的购物车的状态是否为空
        if(!CollectionUtils.isEmpty(userKeyCarts)){
            // 不为空,合并
            userKeyCarts.forEach(userKeyCart -> {
                //有的话更新数量
                if (userIdOps.hasKey(userKeyCart.getSkuId().toString())) {
                    String cartJson = userIdOps.get(userKeyCart.getSkuId().toString()).toString();
                    // 新的购物车
                    Cart idCart = JSON.parseObject(cartJson, Cart.class);
                    //更新数量
                    idCart.setCount(idCart.getCount()+userKeyCart.getCount());
                    userIdOps.put(userKeyCart.getSkuId().toString(),JSON.toJSONString(idCart));
                }else {
                    // 没有就新增记录
                    userIdOps.put(userKeyCart.getSkuId().toString(),JSON.toJSONString(userKeyCart));
                }
            });
            // 合并完成之后,删除未登录的购物车
            this.redisTemplate.delete(key1);
        }
        //查询返回
        // 查询返回
        List<Object> userIdCartJsonList = userIdOps.values();
        if(CollectionUtils.isEmpty(userIdCartJsonList)){
            return null;
        }
        return userIdCartJsonList.stream().map(userIdCartJson -> {
            Cart cart = JSON.parseObject(userIdCartJson.toString(), Cart.class);
            cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX+cart.getSkuId())));
            return cart;
        }).collect(Collectors.toList());

    }





    //修改购物车
    public void updateCart(Cart cart) {
        String key = getKey();
        Integer count = cart.getCount();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        if(hashOps.hasKey(cart.getSkuId().toString())){
            // 获取购物车中更新数量的购物记录
            String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
            cart = JSON.parseObject(cartJson,Cart.class);
            cart.setCount(count);
            hashOps.put(cart.getSkuId().toString(),JSON.toJSONString(cart));

        }

    }

    //删除购物车
    public void deleteCart(Long skuId) {
        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        if(hashOps.hasKey(skuId.toString())){
            hashOps.delete(skuId.toString());
        }
    }

    public void checkCart(List<Cart> carts) {

        // 获取key
        String key = getKey();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        carts.forEach(cart -> {
            Boolean check = cart.getCheck();
            // 判断id 是否存在
            if(hashOps.hasKey(cart.getSkuId().toString())){
                String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
                cart = JSON.parseObject(cartJson,Cart.class);
                cart.setCheck(check);
                hashOps.put(cart.getSkuId().toString(),JSON.toJSONString(cart));
            }
        });
    }


    private String getKey() {
        String key = KEY_PREFIX;
        UserInfo userInfo = LoginInterceptor.get();
        //判断登录状态
        if(userInfo.getUserId()!=null){
            key+=userInfo.getUserId();
        }else {
            key+=userInfo.getUserKey();
        }
        return key;
    }


    public List<CartItemVO> queryCartItemVO(Long userId) {
        //登录,查询登录状态的购物车
        String key = KEY_PREFIX+userId;
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        //查询返回
        List<Object> userIdCartJsonList = hashOps.values();
        if(CollectionUtils.isEmpty(userIdCartJsonList)){
            return null;
        }
        // 获取所有购物车的记录
        return userIdCartJsonList.stream().map(userIdCartJson->{
            Cart cart = JSON.parseObject(userIdCartJson.toString(), Cart.class);
            cart.setCurrentPrice(new BigDecimal(this.redisTemplate.opsForValue().get(CURRENT_PRICE_PREFIX+cart.getSkuId())));
            return cart;
        }).filter(cart -> cart.getCheck()).map(cart -> {
            CartItemVO cartItemVO = new CartItemVO();
            cartItemVO.setSkuId(cart.getSkuId());
            cartItemVO.setCount(cart.getCount());
            return cartItemVO;
        }).collect(Collectors.toList());
    }



}