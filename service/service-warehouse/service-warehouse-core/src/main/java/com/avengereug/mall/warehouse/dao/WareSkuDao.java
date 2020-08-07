package com.avengereug.mall.warehouse.dao;

import com.avengereug.mall.warehouse.dto.StockInfoDTO;
import com.avengereug.mall.warehouse.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 10:59:40
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("actualSkuNum") Integer actualSkuNum);

    List<StockInfoDTO> listStockGroupBySkuId(@Param("skuIds") List<Long> skuIds);
}
