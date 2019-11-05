package com.atguigu.gmall.search;

import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.vo.GoodsVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Autowired
    GmallPmsClient gmallPmsClient;

    @Autowired
    GmallWmsClient gmallWmsClient;

    /**
     * 导入数据
     */
    @Test
    public void importData(){
        Long pageNum = 1l;
        Long pageSize = 100l;

        do{
            //分页查询spu
            QueryCondition condition = new QueryCondition();
            condition.setPage(pageNum);
            condition.setLimit(pageSize);
            Resp<List<SpuInfoEntity>> listResp = gmallPmsClient.querySpuPage(condition);
            //获取当前页spunInfo数据
            List<SpuInfoEntity> spuInfoEntities = listResp.getData();

            // 遍历spu获取spu下的所有sku导入到索引库中
            for (SpuInfoEntity spuInfoEntity : spuInfoEntities) {
                Resp<List<SkuInfoEntity>> skuResp = this.gmallPmsClient.querySkuBySpuId(spuInfoEntity.getId());
                List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
                if(CollectionUtils.isEmpty(skuInfoEntities)){
                    continue;
                }
                skuInfoEntities.forEach(skuInfoEntity -> {
                    GoodsVO goodsVO = new GoodsVO();
                    //设置sku相关的属性
                    goodsVO.setName(skuInfoEntity.getSkuTitle());
                    //设置skuId
                    goodsVO.setId(skuInfoEntity.getSkuId());
                    //设置默认图片
                    goodsVO.setPic(skuInfoEntity.getSkuDefaultImg());
                    //设置价格
                    goodsVO.setPrice(skuInfoEntity.getPrice());
                    //销量
                    goodsVO.setSale(100);
                    //总和排序
                    goodsVO.setSort(0);

                    //设置品牌相关的
                    Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.queryBrandByID(skuInfoEntity.getBrandId());
                    BrandEntity brandEntity = brandEntityResp.getData();
                    if(brandEntity!=null){
                        goodsVO.setBrandId(skuInfoEntity.getBrandId());
                        goodsVO.setBrandName(brandEntity.getName());
                    }

                    //设置分类相关的
                    Resp<CategoryEntity> categoryEntityResp = this.gmallPmsClient.queryCategoryById(skuInfoEntity.getCatalogId());
                    CategoryEntity categoryEntity = categoryEntityResp.getData();
                    if(categoryEntity!=null){
                        goodsVO.setProductCategoryId(skuInfoEntity.getCatalogId());
                        goodsVO.setProductCategoryName(categoryEntity.getName());
                    }

                    //设置搜索属性
                    Resp<List<SpuAttributeValueVO>> searchAttrValueResp = this.gmallPmsClient.querySearchAttrValue(spuInfoEntity.getId());
                    List<SpuAttributeValueVO> spuAttributeValueVOList = searchAttrValueResp.getData();
                    goodsVO.setAttrValueList(spuAttributeValueVOList);

                    //库存
                    Resp<List<WareSkuEntity>> resp = this.gmallWmsClient.queryWareBySkuId(skuInfoEntity.getSkuId());
                    List<WareSkuEntity> wareSkuEntities = resp.getData();
                    if(wareSkuEntities.stream().anyMatch(t->t.getStock()>0)){
                        goodsVO.setStock(1l);
                    }else {
                        goodsVO.setStock(0l);
                    }
                    Index index = new Index.Builder(goodsVO).index("goods").type("info").id(skuInfoEntity.getSkuId().toString()).build();
                    try {
                        this.jestClient.execute(index);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            }
            //获取当前页的记录数
            pageSize = Long.valueOf(spuInfoEntities.size());
            //下一页
            pageNum++;
        }while(pageSize==100);

    }

















    /**
     * 有该记录就更新,没有则新增(以id判断标准)
     * 会把没有设置值的字段更新为null
     */
    @Test
    public void create() throws IOException {
        User user = new User("Tom", 23, "11222");
        Index action = new Index.Builder(user).index("user").type("info").id("2").build();
        DocumentResult result = jestClient.execute(action);
        System.out.println(result.toString());
    }

    /**
     * 仅更新不为null的字段
     */
    @Test
    public void update() throws Exception{
        User user = new User("雷帅", 23, "11222");
        Map<String,Object> map = new HashMap<>();
        map.put("doc",user);
        Update action = new Update.Builder(map).index("user").type("info").id("1").build();

        DocumentResult result = jestClient.execute(action);
        System.out.println(result.toString());
    }

    /**
     *  查询
     */
    @Test
    public void search() throws Exception{
        //查询一个
//        Get action = new Get.Builder("user", "1").build();
//        DocumentResult result = jestClient.execute(action);
//        System.out.println(result.getSourceAsObject(User.class,false));


        //查询多个
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {\n" +
                "      \n" +
                "    }\n" +
                "  }\n" +
                "}";
        Search action = new Search.Builder(query).addIndex("user").addType("info").build();
        SearchResult result = jestClient.execute(action);
        System.out.println(result.getSourceAsObject(User.class, false));

        result.getHits(User.class).forEach(hit -> {
            System.out.println(hit.source);
        });

    }

    /**
     * 删除
     */
    @Test
    public void delete() throws IOException {
        Delete action = new Delete.Builder("2").index("user").type("info").build();
        DocumentResult result = jestClient.execute(action);
        System.out.println(result.toString());

    }



}

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
class User{
    private String name;
    private Integer age;
    private String password;
}