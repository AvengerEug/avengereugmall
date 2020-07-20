package com.avengereug.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.coupon.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * 专题商品
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 17:38:47
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

