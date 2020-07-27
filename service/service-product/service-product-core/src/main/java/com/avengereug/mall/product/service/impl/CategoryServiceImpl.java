package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.common.anno.GlobalTransactional;
import com.avengereug.mall.product.service.CategoryBrandRelationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.product.dao.CategoryDao;
import com.avengereug.mall.product.entity.CategoryEntity;
import com.avengereug.mall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        // 1. 查出所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        // 2. 组装成树形结构
        // 2.1、找出所有的一级分类
        Long start = System.currentTimeMillis();
        logger.info("开始时间：{}", start);
        List<CategoryEntity> result = categoryEntities
                .stream()
                .filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                .map((item) -> {
                    item.setChildren(getChildren(item, categoryEntities));
                    return item;
                })
                .sorted(Comparator.comparingInt(item -> (item.getSort() == null ? 0 : item.getSort())))
                .collect(Collectors.toList());

        Long end = System.currentTimeMillis();
        logger.info("结束时间：{}", end);

        logger.info("耗时：{}", end - start);

        return result;
    }

    /**
     * 获取当前菜单的所有孩子节点(若孩子节点还有孩子节点，则会进行递归获取)，并返回树形结构
     * 缺点：每次遍历一个menu，都要遍历一次all，
     * 所以时间复杂度为：n的平方
     *
     * 优化策略：
     *   虽然都是基于内存去操作，减少了从db的查询，但最好是数据只查询一次，后续直接从redis中取，
     *   若后台对category表有更新，则重新构建属性结构并更新缓存 ==> 先更新db，再删除缓存(保证一致性)
     *
     * jdk1.8流处理，先使用集合开启流，再使用过滤器来过滤部分数据，
     * 再使用map来对数据进行操作，操作完之后进行sorted排序，最后
     * 组装成list
     *
     * 使用流式处理时，也要注意item为null的情况
     *
     * @param current
     * @param all
     * @return
     */
    private final List<CategoryEntity> getChildren(final CategoryEntity current, List<CategoryEntity> all) {
        return all.stream()
                // 找出当前item的所有子菜单
                .filter(categoryEntity -> categoryEntity.getParentCid() == current.getCatId())
                .map((item) -> {
                    // 针对每个子菜单再设置它的子菜单 ==> 递归调用
                    item.setChildren(getChildren(item, all));
                    return item;
                })
                .sorted(Comparator.comparingInt(item -> (item.getSort() == null ? 0 : item.getSort())))
                .collect(Collectors.toList());
    }

    @Override
    public void removeCategoryByIds(List<Long> asList) {
        //TODO 1. 检查当前删除的菜单是否被别的地方引用

        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public List<Long> findCatelogPath(Long catelogId) {
        List<Long> list = new ArrayList<>();
        findParentPath(catelogId, list);
        // 逆序返回
        Collections.reverse(list);
        return list;
    }

    @GlobalTransactional
    @Override
    public void updateCascade(CategoryEntity category) {
        // 更新自己
        this.updateById(category);

        // 更新其他冗余的分类名称
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());

        // TODO 更新剩余的冗余的分类名称

    }

    /**
     * 传入225
     *
     * 返回[225, 34, 2]
     * @param catelogId
     * @param list
     */
    private void findParentPath(Long catelogId, List<Long> list) {
        list.add(catelogId);
        CategoryEntity entity = this.getById(catelogId);
        // DB中设计了一级分类的parentCid为0
        if (entity.getParentCid() != 0) {
            findParentPath(entity.getParentCid(), list);
        }
    }
}