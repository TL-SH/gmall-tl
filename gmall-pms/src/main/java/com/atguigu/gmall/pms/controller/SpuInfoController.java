package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.gmall.pms.service.SpuInfoService;




/**
 * spu信息
 * @author leishuai
 */
@Api(tags = "spu信息 管理")
@RestController
@RequestMapping("pms/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private AmqpTemplate amqpTemplate;


    @ApiOperation("查询商品的列表")
    @GetMapping
    public Resp<PageVo> querySpuInfoByKeyPage(@RequestParam(value = "catId",defaultValue = "0")Long catId,QueryCondition condition){

        PageVo pageVo = spuInfoService.querySpuInfoByKeyPage(catId,condition);

        return Resp.ok(pageVo);
    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('pms:spuinfo:list')")
    public Resp<List<SpuInfoEntity>> querySpuPage(@RequestBody QueryCondition queryCondition) {
        PageVo page = spuInfoService.queryPage(queryCondition);

        return Resp.ok((List<SpuInfoEntity>)page.getList());
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:spuinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = spuInfoService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('pms:spuinfo:info')")
    public Resp<SpuInfoEntity> info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return Resp.ok(spuInfo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:spuinfo:save')")
    public Resp<Object> save(@RequestBody SpuInfoVO spuInfoVO){
		spuInfoService.bigSave(spuInfoVO);
        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:spuinfo:update')")
    public Resp<Object> update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);
        this.sendMsg(spuInfo.getId(),"update");
        return Resp.ok(null);
    }

    //封装发送信息的公用方法
    public void sendMsg(Long spuId,String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",spuId);
        map.put("type",type);
        amqpTemplate.convertAndSend("GMALL-ITEM-EXCHANGE","item."+type,map);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:spuinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return Resp.ok(null);
    }

}
