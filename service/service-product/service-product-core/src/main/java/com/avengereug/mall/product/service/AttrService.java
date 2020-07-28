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

    /**
     * @param params 分页参数及搜索的key
     * @param catelogId 0 => 不匹配catelogId， 否则匹配
     * @param type 需要查询哪种销售属性，0 => 销售属性， 1 => 基本属性
     * @return
     */
    PageUtils queryBaseAttrListPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrRespVoById(Long attrId);

    void updateDetail(AttrVo attrVo);


}

