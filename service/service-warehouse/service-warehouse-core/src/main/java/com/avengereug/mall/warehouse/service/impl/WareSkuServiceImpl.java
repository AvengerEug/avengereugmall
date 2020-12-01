package com.avengereug.mall.warehouse.service.impl;

import com.avengereug.mall.common.exception.NoStockException;
import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.order.vo.OrderItemVo;
import com.avengereug.mall.order.vo.WareSkuLockVo;
import com.avengereug.mall.product.feign.SkuInfoClient;
import com.avengereug.mall.product.vo.SkuInfoEntityVO;
import com.avengereug.mall.warehouse.dto.StockInfoDTO;
import com.avengereug.mall.warehouse.entity.WareOrderTaskDetailEntity;
import com.avengereug.mall.warehouse.entity.WareOrderTaskEntity;
import com.avengereug.mall.warehouse.service.WareOrderTaskDetailService;
import com.avengereug.mall.warehouse.service.WareOrderTaskService;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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

import com.avengereug.mall.warehouse.dao.WareSkuDao;
import com.avengereug.mall.warehouse.entity.WareSkuEntity;
import com.avengereug.mall.warehouse.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private SkuInfoClient skuInfoClient;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();

        /**
         *    wareId: 123,//仓库id
         *    skuId: 123//商品id
         */
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer actualSkuNum) {
        // 先统计下db中是否存在对应的库存信息，因为支持在管理后台手动添加库存信息
        int count = this.count(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (count > 0) {
            // TODO， 需要考虑下这和扣减的sql语句在高并发情况下会发生的情况，是否要添加一个乐观锁(版本号)来确定
            baseMapper.addStock(skuId, wareId, actualSkuNum);
        } else {
            WareSkuEntity entity = new WareSkuEntity();
            entity.setSkuId(skuId);
            entity.setStock(actualSkuNum);
            entity.setWareId(wareId);
            entity.setStockLocked(0);
            // 远程获取skuName, 这仅仅是一个冗余字段，没必要因为远程服务的调用超时等原因而引起的异常而导致事务回滚
            // TODO 有更优雅的方式解决！！！待解决
            try {
                RPCResult<SkuInfoEntityVO> skuInfoEntityVORPCResult = skuInfoClient.innerInfo(skuId);
                if (skuInfoEntityVORPCResult.getCode() == 0) {
                    SkuInfoEntityVO result = skuInfoEntityVORPCResult.getResult();
                    entity.setSkuName(result.getSkuName());
                }
            } catch (Exception e) {
                // Do nothing
            }
            baseMapper.insert(entity);
        }

    }

    @Override
    public Map<Long, Boolean> stockInfo(List<Long> skuIds) {
        List<StockInfoDTO> stockInfoDTOList = baseMapper.listStockGroupBySkuId(skuIds);
        Map<Long, Boolean> map = stockInfoDTOList.stream()
                .collect(Collectors.toMap(key -> key.getSkuId(), value -> value.getStock() == null ? false : value.getStock() > 0));

        return map;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean orderLockStock(WareSkuLockVo vo) {
        /**
         * 保存库存工作单详情信息
         * 追溯
         */
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskService.save(wareOrderTaskEntity);


        //1、按照下单的收货地址，找到一个就近仓库，锁定库存
        //2、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = vo.getLocks();

        List<SkuWareHasStock> collect = locks.stream().map((item) -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            stock.setSkuId(skuId);
            stock.setNum(item.getCount());
            //查询这个商品在哪个仓库有库存
            List<Long> wareIdList = baseMapper.listWareIdHasSkuStock(skuId);
            stock.setWareId(wareIdList);

            return stock;
        }).collect(Collectors.toList());

        //2、锁定库存
        for (SkuWareHasStock hasStock : collect) {
            boolean skuStocked = false;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();

            if (org.springframework.util.StringUtils.isEmpty(wareIds)) {
                //没有任何仓库有这个商品的库存
                throw new NoStockException(skuId);
            }

            //1、如果每一个商品都锁定成功,将当前商品锁定了几件的工作单记录发给MQ
            //2、锁定失败。前面保存的工作单信息都回滚了。发送出去的消息，即使要解锁库存，由于在数据库查不到指定的id，所有就不用解锁
            for (Long wareId : wareIds) {
                //锁定成功就返回1，失败就返回0
                Long count = baseMapper.lockSkuStock(skuId,wareId,hasStock.getNum());
                if (count == 1) {
                    skuStocked = true;
                    WareOrderTaskDetailEntity taskDetailEntity = WareOrderTaskDetailEntity.builder()
                            .skuId(skuId)
                            .skuName("")
                            .skuNum(hasStock.getNum())
                            .taskId(wareOrderTaskEntity.getId())
                            .wareId(wareId)
                            .lockStatus(1)
                            .build();
                    wareOrderTaskDetailService.save(taskDetailEntity);

                    //TODO 告诉MQ库存锁定成功
//                    StockLockedTo lockedTo = new StockLockedTo();
//                    lockedTo.setId(wareOrderTaskEntity.getId());
//                    StockDetailTo detailTo = new StockDetailTo();
//                    BeanUtils.copyProperties(taskDetailEntity,detailTo);
//                    lockedTo.setDetailTo(detailTo);
//                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                    break;
                } else {
                    //当前仓库锁失败，重试下一个仓库
                }
            }

            if (skuStocked == false) {
                //当前商品所有仓库都没有锁住
                throw new NoStockException(skuId);
            }
        }

        //3、肯定全部都是锁定成功的
        return true;
    }


    @Data
    class SkuWareHasStock {
        private Long skuId;
        private Integer num;
        private List<Long> wareId;
    }

}