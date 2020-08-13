package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.product.entity.ProductAttrValueEntity;
import com.avengereug.mall.product.service.ProductAttrValueService;
import com.avengereug.mall.product.vo.AttrRespVO;
import com.avengereug.mall.product.vo.AttrVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.service.AttrService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 商品属性
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 获取基本/销售属性列表
     *
     * /base/list/{catelogId} ==> 获取基本属性
     * /sale/list/{catelogId} ==> 获取销售属性
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    //@RequiresPermissions("product:attr:list")
    public R saleAttrList(
            @RequestParam Map<String, Object> params,
            @PathVariable("catelogId") Long catelogId,
            @PathVariable("attrType") String attrType){
        PageUtils page = attrService.queryBaseAttrListPage(params, catelogId, attrType);

        return R.ok().put("page", page);
    }


    /**
     * 获取spu的基本属性  <---> spu的规格
     *
     * /base/listforspu/1
     *
     */
    @GetMapping("/base/listforspu/{spuId}")
    //@RequiresPermissions("product:attr:list")
    public R getSpuBaseAttr(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> productAttrValueEntityList = attrService.getSpuBaseAttr(spuId);

        return R.ok().put("data", productAttrValueEntityList);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{attrId}")
    //@RequiresPermissions("product:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVO attrRespVo = attrService.getAttrRespVoById(attrId);

        return R.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:attr:save")
    public R save(@RequestBody AttrVO attrVo){
        attrService.saveDetail(attrVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:attr:update")
    public R update(@RequestBody AttrVO attrVo){
        attrService.updateDetail(attrVo);

        return R.ok();
    }

    /**
     * 更新spu的规格参数(基础属性)
     */
    @PutMapping("/update/{spuId}")
    //@RequiresPermissions("product:attr:update")
    public R update(
            @PathVariable("spuId") Long spuId,
            @RequestBody List<ProductAttrValueEntity> productAttrValueEntity){
        productAttrValueService.updateSpuBaseAttr(spuId, productAttrValueEntity);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }


    @GetMapping("/inner/info/{attrId}")
    public RPCResult<AttrRespVO> attrInfo(@PathVariable("attrId") Long attrId) {
        AttrRespVO attrRespVo = attrService.getAttrRespVoById(attrId);

        return new RPCResult<AttrRespVO>().ok(attrRespVo);
    }

}
