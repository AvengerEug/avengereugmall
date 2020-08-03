package com.avengereug.mall.warehouse.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.avengereug.mall.common.controller.BaseController;
import com.avengereug.mall.warehouse.vo.PurchaseDoneVO;
import com.avengereug.mall.warehouse.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.avengereug.mall.warehouse.entity.PurchaseEntity;
import com.avengereug.mall.warehouse.service.PurchaseService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.R;



/**
 * 采购信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 10:59:40
 */
@RestController
@RequestMapping("warehouse/purchase")
public class PurchaseController extends BaseController {

    @Autowired
    private PurchaseService purchaseService;

    /**
     * 列表
     */
    @GetMapping("/list")
    //@RequiresPermissions("warehouse:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     *
     * @param params
     * @return
     */
    @GetMapping("/unreceive/list")
    public R unreceiveList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceivePage(params);

        return R.ok().put("page", page);
    }

    /**
     *
     * 若purchaseId不存在，则新建一个采购单，并关联至新创建的采购单
     *
     */
    @PostMapping("/merge")
    public R mergePurchase(@RequestBody PurchaseMergeVo purchaseMergeVo){
        purchaseService.mergePurchase(purchaseMergeVo);

        return R.ok();
    }

    /**
     * 领取采购单
     * 请求格式：
     * method: post
     * url: /warehouse/purchase/received?assignedId=xxx
     * body:
     *   [1,2,3,4] //采购单id
     *
     * @param purchaseIds
     * @param assignedId
     * @return
     */
    @PostMapping("/receive")
    public R receivePurchase(@RequestBody List<Long> purchaseIds, @RequestParam("assignedId") Long assignedId) {
        purchaseService.receivePurchase(purchaseIds, assignedId);

        return R.ok();
    }

    /**
     * 完成采购单
     * 请求格式：
     * method: post
     * url: /warehouse/purchase/done
     * body:
     *   {
     *    purchaseId: 123, //采购单id
     *    purchaseDetailsItem: [ {purchaseDetailId:1, status:4, reason:"", actualSkuNum: 8} ]//完成/失败的需求详情
     * }
     *
     * @return
     */
    @PostMapping("/done")
    public R done(@Validated @RequestBody PurchaseDoneVO purchaseDoneVO) {
        purchaseService.done(purchaseDoneVO);

        return R.ok();
    }

    /**
     * 信息 - byId
     */
    @GetMapping("/info/{id}")
    //@RequiresPermissions("warehouse:purchase:info")
    public R info(@PathVariable("id") Long id){
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    //@RequiresPermissions("warehouse:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchaseService.saveDetail(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    //@RequiresPermissions("warehouse:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     *
     * TODO 删除的逻辑应该还要校验当前删除的采购单是否关联了
     * 采购需求，如果关联了，应该要提示不能删除。或者
     * 将采购需求关联采购单的id置为null
     *
     *
     *
     */
    @DeleteMapping("/delete")
    //@RequiresPermissions("warehouse:purchase:delete")
    public R delete(@RequestBody Long[] ids){
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
