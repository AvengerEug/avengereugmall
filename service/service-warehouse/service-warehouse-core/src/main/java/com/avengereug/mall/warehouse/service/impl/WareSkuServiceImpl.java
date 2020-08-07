package com.avengereug.mall.warehouse.service.impl;

import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.product.feign.SkuInfoClient;
import com.avengereug.mall.product.vo.SkuInfoEntityVO;
import com.avengereug.mall.warehouse.dto.StockInfoDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private SkuInfoClient skuInfoClient;

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

}