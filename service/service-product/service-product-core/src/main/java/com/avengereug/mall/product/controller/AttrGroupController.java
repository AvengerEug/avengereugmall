package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.avengereug.mall.product.entity.AttrEntity;
import com.avengereug.mall.product.service.AttrService;
import com.avengereug.mall.product.service.CategoryService;
import com.avengereug.mall.product.vo.AttrGroupRelationVO;
import com.avengereug.mall.product.vo.AttrGroupWithAttrVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.entity.AttrGroupEntity;
import com.avengereug.mall.product.service.AttrGroupService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 属性分组
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @GetMapping("/list/{catelogId}")
    //@RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params, @PathVariable Long catelogId){
        // PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params, catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{attrGroupId}")
    //@RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        attrGroup.setCatelogPath(categoryService.findCatelogPath(attrGroup.getCatelogId()));

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 根据分组信息，获取它关联的基本属性
     */
    @GetMapping("/{attrGroupId}/attr/relation")
    //@RequiresPermissions("product:attrgroup:info")
    public R relationInfo(@PathVariable("attrGroupId") Long attrGroupId){
        List<AttrEntity> attrEntityList = attrGroupService.relationInfo(attrGroupId);
        return R.ok().put("data", attrEntityList);
    }

    /**
     * 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联
     */
    @GetMapping("/{attrGroupId}/noattr/relation")
    //@RequiresPermissions("product:attrgroup:info")
    public R noRelationAttrInfo(@RequestParam Map<String, Object> params, @PathVariable("attrGroupId") Long attrGroupId){
        PageUtils pages = attrGroupService.noRelationInfoPage(params, attrGroupId);
        return R.ok().put("page", pages);
    }

    @PostMapping("/attr/relation")
    public R addRelationWithAttrGroup(@RequestBody List<AttrGroupRelationVO> attrGroupRelationVo) {
        attrGroupService.addRelation(attrGroupRelationVo);

        return R.ok();
    }


    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    /**
     * 删除属性与分组的关联关系
     */
    @DeleteMapping("/attr/relation/delete")
    //@RequiresPermissions("product:attrgroup:delete")
    public R deleteAttrAttrgroupRelation(@RequestBody List<AttrGroupRelationVO> relationVos){
        attrGroupService.deleteRelation(relationVos);

        return R.ok();
    }


    /**
     * 获取分类下所有分组&关联属性
     */
    @GetMapping("/{catelogId}/withattr")
    public R findAllAttrGroupWithAttrs(@PathVariable("catelogId") Long catelogId) {
        // 1. 获取当前catelogId下的所有分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(
                new QueryWrapper<AttrGroupEntity>()
                        .eq("catelog_id", catelogId)
        );

        // 2. 找出当前group下关联的所有attr
        List<AttrGroupWithAttrVO> attrGroupWithAttrVOS = attrGroupEntities.stream().map(item -> {
            AttrGroupWithAttrVO vo = new AttrGroupWithAttrVO();
            BeanUtils.copyProperties(item, vo);
            List<AttrEntity> attrEntities = attrGroupService.findAttrByAttrGroupId(item.getAttrGroupId());

            vo.setAttrs(attrEntities);
            return vo;
        }).collect(Collectors.toList());

        return R.ok().put("data", attrGroupWithAttrVOS);
    }

}
