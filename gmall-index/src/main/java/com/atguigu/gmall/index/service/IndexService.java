package com.atguigu.gmall.index.service;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVO;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author tanglei
 */
@Service
public class IndexService {

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private JedisPool jedisPool;


    private static final String KEY_PREFIX = "index:category:";

    public List<CategoryEntity> queryLevel1Category() {

        Resp<List<CategoryEntity>> resp = this.gmallPmsClient.queryCategory(1, null);
        return resp.getData();
    }

    public List<CategoryVO> queryCategoryVO(Long pid) {

        //1.查询缓存,缓存有的话直接返回
        String cache = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if(StringUtils.isNotBlank(cache)){
            return JSON.parseArray(cache,CategoryVO.class);
        }
        // 2. 如果缓存中没有，查询数据库
        Resp<List<CategoryVO>> listResp = this.gmallPmsClient.querySubCategory(pid);
        List<CategoryVO> categoryVOS = listResp.getData();

        // 3. 查询之后,放入缓存
        this.redisTemplate.opsForValue().set(KEY_PREFIX + pid,JSON.toJSONString(categoryVOS));

        return categoryVOS;
    }


    public String testLock2(){

        //只要锁名相同就是同一把锁
        RLock lock = redissonClient.getLock("lock");
        //加锁
        lock.lock();
        // 获取到锁执行业务逻辑
        String numString = this.redisTemplate.opsForValue().get("num");
        if (StringUtils.isBlank(numString)) {
            return null;
        }
        int num = Integer.parseInt(numString);
        this.redisTemplate.opsForValue().set("num", String.valueOf(++num));
        //释放锁
        lock.unlock();
        return "已经增加成功";
    }

    public String testLock() {
        //保证锁的唯一性  加锁
        String uuid = UUID.randomUUID().toString();
        Boolean lock = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 10, TimeUnit.SECONDS);
        if(lock){
            // 获取到锁执行业务逻辑
            String numString = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(numString)) {
                return null;
            }
            int num = Integer.parseInt(numString);
            this.redisTemplate.opsForValue().set("num", String.valueOf(++num));

            //释放锁
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script,Arrays.asList("lock"),Arrays.asList(uuid));

            }finally {
                if (jedis != null) {
                    jedis.close();
                }
            }

            //使用lua脚本   释放锁
            //String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            //this.redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList("lock"),Arrays.asList(uuid));


            //释放锁
            /*if(StringUtils.equals(uuid,this.redisTemplate.opsForValue().get("lock"))){
                this.redisTemplate.delete("lock");
            }*/

        }else {
            //没有获取到锁就进行重试
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                testLock();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "已经增加成功";
    }


    public String testRead() {

        return null;
    }

    public String testWrite() {

        return null;
    }
}
