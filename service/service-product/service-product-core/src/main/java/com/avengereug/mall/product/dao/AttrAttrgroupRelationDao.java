package com.avengereug.mall.product.dao;

import com.avengereug.mall.product.entity.AttrAttrgroupRelationEntity;
import com.avengereug.mall.product.vo.AttrGroupRelationVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteBatchRelation(@Param("entities") List<AttrAttrgroupRelationEntity> entityList);

    void insertBatch(@Param("entities") List<AttrAttrgroupRelationEntity> entities);

    List<Long> selectAttrIds(@Param("attrGroupId") Long attrGroupId);
}
