package com.avengereug.mall.warehouse.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.avengereug.mall.common.Enum.BusinessCodeEnum;
import com.avengereug.mall.common.exception.NoStockException;
import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.order.vo.WareSkuLockVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.warehouse.entity.WareSkuEntity;
import com.avengereug.mall.warehouse.service.WareSkuService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;


/**
 * 商品库存
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 10:59:40
 */
@RestController
@RequestMapping("warehouse/waresku")
@Slf4j
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("warehouse:waresku:info")
    public R info(@PathVariable("id") Long id){
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("warehouse:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("warehouse:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("warehouse:waresku:delete")
    public R delete(@RequestBody Long[] ids){
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/inner/has-stock")
    public RPCResult<Map<Long, Boolean>> innerHasStock(@RequestBody List<Long> skuIds) {
        Map<Long, Boolean> stockInfo = wareSkuService.stockInfo(skuIds);

        return new RPCResult<Map<Long, Boolean>>().ok(stockInfo);
    }

    @PostMapping(value = "/lock/order")
    public Boolean orderLockStock(@RequestBody WareSkuLockVo vo) {
        try {
            boolean lockStock = wareSkuService.orderLockStock(vo);
            return lockStock;
        } catch (NoStockException e) {
            log.error(BusinessCodeEnum.NO_STOCK_EXCEPTION.getMsg(), ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

}
