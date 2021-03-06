package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.atguigu.gmall.pms.vo.GroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;




/**
 * 属性分组
 *
 * @author leishuai
 * @date 2019-10-28 20:21:21
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 根据三级分类和spuId查询group
     * @param cid
     * @param spuId
     * @return
     */
    @GetMapping("item/group/{cid}/{spuId}")
    public Resp<List<GroupVO>> queryGroupVOByCid(@PathVariable("cid")Long cid,@PathVariable("spuId")Long spuId){
        List<GroupVO> groupVOS = this.attrGroupService.queryGroupVOByCid(cid,spuId);
        return Resp.ok(groupVOS);
    }


    @ApiOperation("根据三级分类查询分组及组下的信息")
    @GetMapping("withattrs/cat/{catId}")
    public Resp<List<AttrGroupVO>> queryGroupWithAttrsByCid(@PathVariable(value = "catId")Long catId){
        List<AttrGroupVO> attrGroupVOList = this.attrGroupService.queryGroupWithAttrsByCid(catId);
        return Resp.ok(attrGroupVOList);
    }


    @ApiOperation("根据分组id查询分组以下的规格数据")
    @GetMapping("withattr/{gid}")
    public Resp<AttrGroupVO> queryById(@PathVariable(value = "gid")Long gid){
        AttrGroupVO attrGroupVO =attrGroupService.queryById(gid);
        return Resp.ok(attrGroupVO);
    }



    @ApiOperation("根据三级分类id查询分页")
    @GetMapping("{catId}")
    public Resp<PageVo> queryByCidPage(@PathVariable(value = "catId")Long catId,QueryCondition condition){
        PageVo pageVo = attrGroupService.queryByCidPage(catId,condition);
        return Resp.ok(pageVo);
    }
    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:attrgroup:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = attrGroupService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{attrGroupId}")
    @PreAuthorize("hasAuthority('pms:attrgroup:info')")
    public Resp<AttrGroupEntity> info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return Resp.ok(attrGroup);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:attrgroup:save')")
    public Resp<Object> save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:attrgroup:update')")
    public Resp<Object> update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:attrgroup:delete')")
    public Resp<Object> delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return Resp.ok(null);
    }

}
