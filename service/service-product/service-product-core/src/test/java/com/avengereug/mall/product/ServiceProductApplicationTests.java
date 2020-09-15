package com.avengereug.mall.product;

import com.avengereug.mall.product.dao.AttrGroupDao;
import com.avengereug.mall.product.entity.BrandEntity;
import com.avengereug.mall.product.service.BrandService;
import com.avengereug.mall.product.vo.SkuItemVO;
import com.avengereug.mall.product.vo.SpuItemAttrGroupVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.List;

@SpringBootTest
class ServiceProductApplicationTests {

    @Autowired
    private BrandService brandService;

//    @Autowired
//    private OSS ossClient;

    /**
     * 1. aliyun oss client安装:
     *    https://help.aliyun.com/document_detail/32009.html?spm=a2c4g.11186623.6.797.4c5e2841fFbIlx
     * 2. 文件上传
     *    https://help.aliyun.com/document_detail/84781.html?spm=a2c4g.11186623.6.819.53cc6328sFtwq6#title-ia7-h6m-hii
     *
     * 3. 配置accessKeyId和accessKeySecret:
     *    建议以RAM 子账号的方式创建, 创建完子账号后，还需要为它设置操作oss的权限，随后生成accessKeyId和accessKeySecret
     *    注意: accessKeyId和accessKeySecret生成后，无法再次查看，所以需要记住。同时，为了避免误操作，aliyun支持多次生成
     *
     * =========>
     *
     * 推荐使用aliyun开发的 spring-cloud-oss 组件来无缝对接spring cloud：
     * 地址：https://github.com/alibaba/spring-cloud-alibaba/wiki/OSS
     *
     */
    @Test
    void testUploadToAliyunOss() throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        // 上传文件流。
//        InputStream inputStream = new FileInputStream("G:\\壁纸\\1533391936442.jpg");
//        ossClient.putObject("avenger-mall", "1533391936442.jpg", inputStream);
//
//        // 关闭OSSClient。
//        ossClient.shutdown();
    }


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

    @Resource
    private AttrGroupDao attrGroupDao;

    @Test
    void testGetAttrGroupWithAttrsBySpuId() {
        List<SpuItemAttrGroupVO> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(1L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

}
