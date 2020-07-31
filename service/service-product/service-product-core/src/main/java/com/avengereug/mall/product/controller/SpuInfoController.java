package com.avengereug.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.avengereug.mall.product.entity.BrandEntity;
import com.avengereug.mall.product.entity.CategoryEntity;
import com.avengereug.mall.product.service.BrandService;
import com.avengereug.mall.product.service.CategoryService;
import com.avengereug.mall.product.vo.SpuInfoSearchVO;
import com.avengereug.mall.product.vo.spusave.SpuSaveVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.product.entity.SpuInfoEntity;
import com.avengereug.mall.product.service.SpuInfoService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * spu信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {

    @Autowired
    private SpuInfoService spuInfoService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("product:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = spuInfoService.queryPage(params);

        // 组装vo, 添加品牌名字、分类名字
        // TODO 疑问：根据id循环去查还是批量查找
        List<SpuInfoEntity> spuInfoEntityList = (List<SpuInfoEntity>) page.getList();
        List<SpuInfoSearchVO> collect = spuInfoEntityList.stream().map(item -> {
            SpuInfoSearchVO vo = new SpuInfoSearchVO();
            BeanUtils.copyProperties(item, vo);

            if (!ObjectUtils.isEmpty(item.getBrandId())) {
                vo.setBrandName(brandService.getName(item.getBrandId()));
            }

            if (!ObjectUtils.isEmpty(item.getCatelogId())) {
                vo.setCatelogName(categoryService.getName(item.getCatelogId()));
            }

            return vo;
        }).collect(Collectors.toList());

        page.setList(collect);
        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("product:spuinfo:info")
    public R info(@PathVariable("id") Long id){
        SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("product:spuinfo:save")
    public R save(@RequestBody SpuSaveVO spuSaveVo){
        spuInfoService.save(spuSaveVo);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("product:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
        spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("product:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
        spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
