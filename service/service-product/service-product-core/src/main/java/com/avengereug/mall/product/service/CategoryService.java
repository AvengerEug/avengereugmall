package com.avengereug.mall.product.service;

import com.avengereug.mall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeCategoryByIds(List<Long> asList);

    /**
     * 找到catelogId的完整路径
     * @param catelogId
     * @return
     */
    List<Long> findCatelogPath(Long catelogId);

    void updateCascade(CategoryEntity category);

    String getName(Long catelogId);

    List<CategoryEntity> getLevel1Categorys();

    Map<String,List<Catelog2Vo>> getCatalogJson();

    Map<String, List<Catelog2Vo>> getCategoryListWithDistributedLock();
}

