package com.avengereug.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.product.entity.SpuImagesEntity;

import java.util.Map;

/**
 * spu图片
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

