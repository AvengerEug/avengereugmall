package com.avengereug.mall.coupon.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.avengereug.mall.coupon.to.MemberPriceTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.coupon.entity.MemberPriceEntity;
import com.avengereug.mall.coupon.service.MemberPriceService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 商品会员价格
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@RestController
@RequestMapping("coupon/memberprice")
public class MemberPriceController {

    @Autowired
    private MemberPriceService memberPriceService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("coupon:memberprice:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberPriceService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("coupon:memberprice:info")
    public R info(@PathVariable("id") Long id){
        MemberPriceEntity memberPrice = memberPriceService.getById(id);

        return R.ok().put("memberPrice", memberPrice);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("coupon:memberprice:save")
    public R save(@RequestBody MemberPriceEntity memberPrice){
        memberPriceService.save(memberPrice);

        return R.ok();
    }

    /**
     * 服务内保存会员价格
     */
    @PostMapping("/inner/save")
    public R innerSaveBatch(@RequestBody List<MemberPriceTO> memberPriceTOS) {
        List<MemberPriceEntity> memberPriceEntityList = memberPriceTOS.stream().map(memberPriceTo -> {
            MemberPriceEntity memberPrice = new MemberPriceEntity();
            memberPrice.setSkuId(memberPriceTo.getSkuId());
            memberPrice.setMemberLevelId(memberPriceTo.getId());
            memberPrice.setMemberLevelName(memberPriceTo.getName());
            memberPrice.setMemberPrice(memberPriceTo.getPrice());
            // TODO 服务内保存会员价格中，需要确定是否叠加其他优惠的逻辑
            //memberPrice.setAddOther();

            return memberPrice;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(memberPriceEntityList);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("coupon:memberprice:update")
    public R update(@RequestBody MemberPriceEntity memberPrice){
        memberPriceService.updateById(memberPrice);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("coupon:memberprice:delete")
    public R delete(@RequestBody Long[] ids){
        memberPriceService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
