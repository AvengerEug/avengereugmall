package com.avengereug.mall.warehouse.service.impl;

import com.avengereug.mall.common.Enum.BusinessCodeEnum;
import com.avengereug.mall.common.anno.GlobalTransactional;
import com.avengereug.mall.common.constants.WarehouseConstant;
import com.avengereug.mall.common.exception.RRException;
import com.avengereug.mall.warehouse.entity.PurchaseDetailEntity;
import com.avengereug.mall.warehouse.service.PurchaseDetailService;
import com.avengereug.mall.warehouse.vo.PurchaseMergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.warehouse.dao.PurchaseDao;
import com.avengereug.mall.warehouse.entity.PurchaseEntity;
import com.avengereug.mall.warehouse.service.PurchaseService;
import org.springframework.util.CollectionUtils;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(PurchaseEntity purchase) {
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());

        this.save(purchase);
    }

    @GlobalTransactional
    @Override
    public boolean updateById(PurchaseEntity entity) {
        entity.setUpdateTime(new Date());
        return super.updateById(entity);
    }

    @Override
    public PageUtils queryUnreceivePage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);
    }

    @GlobalTransactional
    @Override
    public void mergePurchase(PurchaseMergeVo purchaseMergeVo) {
        Long purchaseId = purchaseMergeVo.getPurchaseId();
        boolean isCreated = false;
        if (isCreated = purchaseId == null) {
            // 新建一个采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WarehouseConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setUpdateTime(new Date());
            purchaseEntity.setCreateTime(new Date());

            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        // 保证当前需要被合并的采购单状态是允许被合并的，即当前采购单的状态是新建或者已分配
        if (!isCreated) {
            PurchaseEntity purchaseEntity = this.getById(purchaseId);
            if (purchaseEntity == null) {
                throw new RRException(
                        BusinessCodeEnum.NOT_ALLOWED_MERGED_CAUSED_BY_ILLEGAL_PURCHASE_ID.getMsg(),
                        BusinessCodeEnum.NOT_ALLOWED_MERGED_CAUSED_BY_ILLEGAL_PURCHASE_ID.getCode());
            }

            if (purchaseEntity.getStatus() > WarehouseConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                throw new RRException(BusinessCodeEnum.NOT_ALLOWED_MERGED_CAUSED_BY_ILLEGAL_STATUS.getMsg(),
                        BusinessCodeEnum.NOT_ALLOWED_MERGED_CAUSED_BY_ILLEGAL_STATUS.getCode());
            }
        }

        List<Long> items = purchaseMergeVo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> purchaseDetailEntityList = items.stream().map(item -> {
            PurchaseDetailEntity entity = new PurchaseDetailEntity();
            entity.setId(item);
            entity.setPurchaseId(finalPurchaseId);
            entity.setStatus(WarehouseConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return entity;
        }).collect(Collectors.toList());
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);

    }

    @GlobalTransactional
    @Override
    public void receivePurchase(List<Long> purchaseIds, Long receivedId) {
        // 1. 拿到所有的采购单
        List<PurchaseEntity> purchaseEntityList = (List<PurchaseEntity>) this.listByIds(purchaseIds);

        if (!CollectionUtils.isEmpty(purchaseEntityList)) {

            purchaseEntityList.forEach(item -> {
                // 2. 验证采购单为已分配状态下的当前领取采购单的人和采购单已分配的人是否为同一人，如果不是，则无法领取
                // 不需要验证新建状态下的采购单，因为新建状态下的采购单，直接就会分配给当前调用此api的人，即形参的receivedId
                if (item.getStatus() == WarehouseConstant.PurchaseStatusEnum.ASSIGNED.getCode()) {
                    if (!receivedId.equals(item.getAssigneeId())) {
                        throw new RRException(
                                BusinessCodeEnum.NOT_ASSIGNED_PURCHASE_CAUSED_BY_NO_SAME_ASSIGNED.getMsg()
                                        + "，采购单id为：" + item.getId(),
                                BusinessCodeEnum.NOT_ASSIGNED_PURCHASE_CAUSED_BY_NO_SAME_ASSIGNED.getCode());
                    }
                }

                // 3. 校验当前采购单的状态是否合法，只能从新建和已分配的状态转成已领取状态
                // 采购单的状态处于非新建和非已分配状态，则抛出异常
                if (item.getStatus() >= WarehouseConstant.PurchaseStatusEnum.RECEIVED.getCode()) {
                    throw new RRException(
                            BusinessCodeEnum.NOT_ASSIGNED_PURCHASE_CAUSED_BY_ILLEGAL_PURCHASE_STATUS.getMsg()
                                    + "，采购单id为：" + item.getId(),
                            BusinessCodeEnum.NOT_ASSIGNED_PURCHASE_CAUSED_BY_ILLEGAL_PURCHASE_STATUS.getCode());
                }

                // 4. 把所有的采购单的状态改为已领取,
                // TODO 采购单要采购的数量和仓库，待采购人员调用完成采购api再更新
                item.setStatus(WarehouseConstant.PurchaseStatusEnum.RECEIVED.getCode());
                item.setUpdateTime(new Date());
                this.updateById(item);

                // 5. 将采购单对应的所有采购需求的状态改为正在采购
                purchaseDetailService.updateStatusByPurchaseId(WarehouseConstant.PurchaseDetailStatusEnum.BUYING.getCode(), item.getId());

            });

        }




    }

}