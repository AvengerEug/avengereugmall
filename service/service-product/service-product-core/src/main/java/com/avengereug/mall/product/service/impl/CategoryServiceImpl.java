package com.avengereug.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.avengereug.mall.common.anno.GlobalTransactional;
import com.avengereug.mall.product.service.CategoryBrandRelationService;
import com.avengereug.mall.product.vo.Catelog2Vo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    private static final String CATEGORY_CACHE_KEY = "categoryList";

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

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

    @Override
    public String getName(Long catelogId) {

        return baseMapper.selectNameByCatelogId(catelogId);
    }


    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        System.out.println("getLevel1Categorys........");
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("消耗时间："+ (System.currentTimeMillis() - l));
        return categoryEntities;
    }

    /**
     * 使用spring-boot-starter-data-redis的
     * stringRedisTemplate保存分类数据时，会出现非常严重的问题，
     * 因为在springboot2.0之后，redisTemplate在初始化的过程中，默认使用的是Lettuce作为redis的客户端，
     * 而Lettuce底层使用的netty通信，又因为netty存储堆外内存的概念，当我们没有显示的
     * 指定netty的对外内存时，它会使用默认的jvm的-Xmx的参数。
     * Lettuce的一些bug，导致netty的堆外内存没有正常释放，在高并发长时间交互redis的情况下，会频繁的进行
     * 反序列化操作，因为反序列化是netty在处理，所以会用到netty的堆外内存，最终会导致netty堆外内存被占满，
     * 进而触发OutOfDirectMemoryError的异常。
     * 在高并发的情况下如果使用Lettuce操作redis，那么这个问题是一定会发生的，只是时间长短问题。
     *
     * 解决方案：
     * 1、升级Lettuce客户端版本，但是好像没有一个很好的解决方案
     * 2、使用jedis客户端来替代Lettuce(直接在xml中配置即可，redisTemplate的自动装配支持根据依赖来决定
     *    redisTemplate底层是使用哪种客户端来与redis进行交互)
     * 3、使用其他的redis客户端来操作，eg：redisson
     *
     * TODO: 后续部署生产环境时，可以把客户端改成Lettuce来定位问题
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        String categoryJSON = stringRedisTemplate.opsForValue().get(CATEGORY_CACHE_KEY);

        if (StringUtils.isNotEmpty(categoryJSON)) {
            return JSON.parseObject(categoryJSON, new TypeReference<Map<String, List<Catelog2Vo>>>() {});
        }

        System.out.println("查询了数据库");

        //将数据库的多次查询变为一次
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1、查出所有分类
        //1、1）查出所有一级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //封装数据
        Map<String, List<Catelog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1、每一个的一级分类,查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2、封装上面的结果
            List<Catelog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1、找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catelog2Vo.Category3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2、封装成指定格式
                            Catelog2Vo.Category3Vo category3Vo = new Catelog2Vo.Category3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        stringRedisTemplate.opsForValue().set(CATEGORY_CACHE_KEY, JSON.toJSONString(parentCid));

        return parentCid;
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

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList,Long parentCid) {
        List<CategoryEntity> categoryEntities = selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
        return categoryEntities;
        // return this.baseMapper.selectList(
        //         new QueryWrapper<CategoryEntity>().eq("parent_cid", parentCid));
    }

}