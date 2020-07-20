package com.avengereug.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

