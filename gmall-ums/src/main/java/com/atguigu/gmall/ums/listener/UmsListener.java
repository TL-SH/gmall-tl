package com.atguigu.gmall.ums.listener;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.vo.UserBoundVO;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author tanglei
 */
@Component
public class UmsListener {

    @Autowired
    private MemberDao memberDao;

    /**
     * 加积分 成长值
     * @param userBoundVO
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "UMS-EXCHANGE-QUEUE", durable = "true"),
            exchange = @Exchange(value = "UMS-EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"order.pay"}
    ))
    public void listener(UserBoundVO userBoundVO){
        memberDao.updateIntegrationAndGrowth(userBoundVO.getUserId(),userBoundVO.getGrouth(),userBoundVO.getIntegration());
    }

}
