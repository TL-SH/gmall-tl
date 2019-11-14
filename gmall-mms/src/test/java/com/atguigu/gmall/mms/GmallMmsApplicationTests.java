package com.atguigu.gmall.mms;

import com.atguigu.gmall.mms.template.SmsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class GmallMmsApplicationTests {

    @Autowired
    SmsTemplate smsTemplate;

    @Test
    public void smsSend(){
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", "18726009750");
        querys.put("param", "code:12425");
        querys.put("tpl_id", "TP1711063");
        smsTemplate.sendSms(querys);
    }
}
