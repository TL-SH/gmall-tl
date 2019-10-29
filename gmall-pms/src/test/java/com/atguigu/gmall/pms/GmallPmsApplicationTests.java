package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.dao.BrandDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallPmsApplicationTests {

    @Autowired
    BrandDao brandDao;

    @Test
    void contextLoads() {
    }

    @Test
    public void test(){
        System.out.println(brandDao.selectList(null));
    }


}
