package com.avengereug.mall.product.service;

import com.avengereug.mall.product.vo.AttrRespVo;
import com.avengereug.mall.product.vo.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.product.entity.AttrEntity;

import java.util.Map;

/**
 * 商品属性
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存attr属性
     *
     * 同时要更新Attr和attrGroup的关联中间表的信息
     *
     *
     * @param attr
     */
    void saveDetail(AttrVo attr);

    PageUtils queryDetail(Map<String, Object> params, Long catelogId);

    AttrRespVo getAttrRespVoById(Long attrId);

    void updateDetail(AttrVo attrVo);
}

