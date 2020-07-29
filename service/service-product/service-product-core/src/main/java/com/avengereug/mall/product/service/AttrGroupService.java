package com.avengereug.mall.product.service;

import com.avengereug.mall.product.entity.AttrEntity;
import com.avengereug.mall.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * catelogId为0时，查找所有 + key的模糊匹配
     * @param params
     * @param catelogId
     * @return
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrEntity> relationInfo(Long attrGroupId);

    void deleteRelation(List<AttrGroupRelationVo> relationVos);

    PageUtils noRelationInfoPage(Map<String, Object> params, Long attrGroupId);

    void addRelation(List<AttrGroupRelationVo> attrGroupRelationVos);
}

