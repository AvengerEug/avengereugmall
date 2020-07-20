package com.avengereug.mall.product;

import com.avengereug.mall.product.entity.BrandEntity;
import com.avengereug.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ServiceProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Test
    void testSave() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("测试自增描述");
        brandEntity.setName("测试品牌名字");
        boolean save = brandService.save(brandEntity);
        System.out.println(save);
        System.out.println(brandEntity.getBrandId());

        System.out.println(brandService.getById(brandEntity.getBrandId()));

    }

    @Test
    void testQuery() {
        List<BrandEntity> list = brandService.query().eq("brand_id", 1).list();
        System.out.println(list);
    }


}
