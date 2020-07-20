package com.avengereug.mall.coupon.dao;

import com.avengereug.mall.coupon.entity.CouponHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券领取历史记录
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
@Mapper
public interface CouponHistoryDao extends BaseMapper<CouponHistoryEntity> {

}
