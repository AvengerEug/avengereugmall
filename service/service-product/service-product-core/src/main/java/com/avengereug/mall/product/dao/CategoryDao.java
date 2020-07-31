package com.avengereug.mall.product.dao;

import com.avengereug.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品三级分类
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    String selectNameByCatelogId(@Param("catelogId") Long catelogId);
}
