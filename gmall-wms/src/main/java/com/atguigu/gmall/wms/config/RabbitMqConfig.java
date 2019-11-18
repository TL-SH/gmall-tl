package com.atguigu.gmall.wms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanglei
 */
@Component
public class RabbitMqConfig {

    /**
     * 交换机
     * @return
     */
    @Bean
    public Exchange exchange(){
        /**
         * 第一个参数:交换机的名称
         * 第二个参数:是否持久化
         * 第三个参数:是否自动删除交换机
         * 第四个参数: 参数
         */
        return new TopicExchange("WMS-EXCHANGE",true,false,null);
    }

    /**
     * 延迟队列
     * @return
     */
    @Bean
    public Queue queue(){
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "WMS-EXCHANGE");
        arguments.put("x-dead-letter-routing-key", "wms.ttl");
        arguments.put("x-message-ttl", 60000);
        return new Queue("WMS-TTL-QUEUE",true,false,false,arguments);
    }

    /**
     * 绑定关系
     * @return
     */
    @Bean
    public Binding binding(){
        return new Binding("WMS-TTL-QUEUE",Binding.DestinationType.QUEUE,"WMS-EXCHANGE","wms.unlock",null);
    }

    /**
     * 死信队列
     * @return
     */
    @Bean
    public Queue deadQueue(){
        return new Queue("WMS-DEAD-QUEUE",true,false,false,null);
    }

    /**
     * 绑定死信队列
     * @return
     */
    @Bean
    public Binding deadBinding(){
        return new Binding("WMS-DEAD-QUEUE",Binding.DestinationType.QUEUE,"WMS-EXCHANGE","wms.ttl",null);
    }






}
