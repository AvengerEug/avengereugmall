package com.avengereug.mall.order.dao;

import com.avengereug.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:09:44
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

}
