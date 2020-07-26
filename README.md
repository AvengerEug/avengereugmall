# mall

#### 介绍
基于SpringCloud + Nacos + Mybatis Plus + renren代码生成器 + MySQL + Redis缓存+ RabbitMQ消息队列 + ES搜索引擎 + ELK + OSS自己搭建企业级电商项目。


#### 一、安装教程

1. clone代码

   ```shell
   git clone https://gitee.com/avengereug/mall.git -b develop
   ```

2.  执行如下代码进行编译

    ```shell
    cd mall && mvn clean -Dmaven.test.skip=true install
    ```

3.  本地需要起nacos服务，具体参考官网：[快速开始](https://nacos.io/zh-cn/docs/quick-start.html)。**ps: 以nacos默认的8848端口开启服务**

4.  启动对应的服务即可

#### 二、各服务端口映射关系

* 详细信息：

  |      服务名       |          作用          | 端口  |                             备注                             |
  | :---------------: | :--------------------: | :---: | :----------------------------------------------------------: |
  |    mall-common    | 各微服务共同依赖的模块 |  无   |                              无                              |
  | renren-generator  |     代码逆向生成器     |  80   | 若需要启动它，需要将根目录的pom文件中的模块依赖加上(<module>renren-generator</module>-->) |
  |  service-coupon   |       优惠券服务       | 7000  |                      连接mall-sms数据库                      |
  |  service-member   |        会员服务        | 8000  |                      连接mall-msm数据库                      |
  |   service-order   |        订单服务        | 9000  |                      连接mall-oms数据库                      |
  |  service-product  |        商品服务        | 10000 |                      连接mall-pms数据库                      |
  | service-warehouse |        仓储服务        | 11000 |                      连接mall-wms数据库                      |

#### 三 nacos的使用

1. 注册中心的使用

    * Nacos的使用，alibaba仅提供了它的客户端，所以我们在使用的时候需要手动将nacos的服务端(下载nacos服务器并启动)启动起来，然后再使用`@EnableDiscoveryClient注解 + 固定配置`来注册
    
      ```txt
      几个注意点：
        1. 引入nacos client相关依赖(根据springcloud版本选择合适的nacos版本)
        2. 启动nacos服务
        3. 配置服务名名称(一定要配置，否则无法注册成功)
        4. 配置nacos注册中心地址
      ```
    
    * 具体参考官网: [nacos集成springcloud](https://nacos.io/zh-cn/docs/quick-start-spring-cloud.html)
    
    * Nacos控制台页面: http://host:port/nacos   **nacos/nacos**
    
2. 配置中心的使用：[参考官网详细链接：nacos-config使用手册](https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-config)

    ps：nacos拥有全局开关、支持yaml文件(需要配置)、拥有namespace、group、dataId等特性。通常我们可以使用namespace + group的粒度来实现服务中环境的隔离(namespace来做服务之间的隔离，用group来做服务中环境的隔离)，或者使用profile的粒度来实现环境的隔离。

3. nacos与spring cloud版本之间的选择：[参考github中wiki详细说明](https://github.com/alibaba/spring-cloud-alibaba/wiki/版本说明)

#### 四、使用aliyun对象存储服务(oss)

* 为什么使用云存储服务(oss)

  ```txt
  当应用是单体架构时，不实用oss服务也没关系，我们直接把文件上传到应用的指定位置即可，然后由应用去访问。但是当请求量上来了之后，一台应用服务器很容易成为系统瓶颈，此时我们需要进行集群部署。在集群部署条件下，把文件上传到应用服务器上的方案将会变得不可取。此时我们可以选择自己搭建nfs文件服务器，然后所有的集群都使用这个nfs服务器，这种方案也是可行的，但是，这将增加了系统的维护成本，我们需要添加nfs服务器的维护人员，前期需要搭建，后期需要维护，比较费人力，因此我们推荐使用云存储服务，按量收费，对于我们系统而言，不需要维护这个文件存储服务，只需要按量付费即可，省时省力。
  ```

* 将文件上传到文件服务器的几种方式

  ```txt
  1. 使用表单将需要上传的文件传到后端，然后再让后端进行转存储至OSS。 ===> 缺点：表单提交时，一般是一个文件流，如果文件特别大，这将会成为一个系统瓶颈的隐患。
  
  2. 前端直接上传到OSS。
     此种模式又分为如下两种:
     2.1 前端直接使用accessKey和accessId直接与oss进行交互 ===> 缺点：不安全，accessKey和accessId都是比较敏感的数据，拥有这两个值就代表着拥有了操作OSS的权限(一般是一个账号对应一个accessKey和accessId)。
     2.2 将accessKey和accessId存在后端，每次前端上传图片前，请求服务端获取临时签名(存在过期时间或者一次性的签名)，然后前端将通过临时签名与OSS进行交互。
  ```


#### 五、自定义JSR303校验注解，允许一个Integer类型的值只能输入指定的值及JSR303规范使用总结

 * 1.创建对应的注解**AllowValues**

   ```java
   /**
    * 校验只能为指定的值
    *
    * JSR303规范：
    * 每一个JSR303注解必须同时包含message、groups、payload方法
    *
    */
   @Documented
   // 此注解指定了AllowVal校验由具体的解析器来处理，一个JSR303规范的注解能使用多个ConstraintValidator来校验
   @Constraint(validatedBy = { AllowValueConstraintValidator.class })
   @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
   @Retention(RUNTIME)
   public @interface AllowValues {
   
       /**
        * 将会在Classpath路径下找ValidationMessages.properties
        * 文件中包含key为com.avengereug.mall.common.utils.jsr303.valid.anno.AllowValues的value
        *
        * @return
        */
       String message() default "{com.avengereug.mall.common.utils.jsr303.valid.anno.AllowValues}";
   
       Class<?>[] groups() default { };
   
       Class<? extends Payload>[] payload() default { };
   
       /**
        * 配置允许的值
        * @return
        */
       int[] value() default { };
   }
   ```

* 2.创建`AllowValueConstraintValidator.java`类作为**AllowValues**注解的处理器

  ```java
  public class AllowValueConstraintValidator implements ConstraintValidator<AllowValues, Integer> {
  
      private Set<Integer> set = new HashSet<>();
  
      /**
       * 获取到注解初始化的值，并存入set中
       * @param constraintAnnotation
       */
      @Override
      public void initialize(AllowValues constraintAnnotation) {
          int[] value = constraintAnnotation.value();
          if (value.length > 0) {
              for (int i : value) {
                  set.add(i);
              }
          }
      }
  
      /**
       * 校验传入的value包含在 @AllowValues中指定的数组中
       *
       * @param value
       * @param context
       * @return
       */
      @Override
      public boolean isValid(Integer value, ConstraintValidatorContext context) {
          return set.contains(value);
      }
  
  }
  ```

* 3.在classpath(maven项目的话，在resources目录)下创建**ValidationMessages.properties**文件，并添加对应的信息

  ```properties
  com.avengereug.mall.common.utils.jsr303.valid.anno.AllowValues=请使用合法的值提交
  ```

* 4.在指定的字段中使用即可

  ```java
  @AllowValues(value = {0, 1})
  private Integer showStatus;
  ```

  上述代码，在controller中结合**@Validated** 修饰对应的实体类，即可完成对应的校验。具体参考**com.avengereug.mall.product.controller.BrandController#updateStatus**方法

* JSR303规范高级功能 - 分组

  以如下代码可知：

  ```java
  // BrandController
  public R updateStatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand){
      brandService.updateById(brand);
      return R.ok();
  }
  
  // BrandEntity
  @Data
  @TableName("pms_brand")
  public class BrandEntity implements Serializable {
  
      private static final long serialVersionUID = 1L;
  
      /**
       * 品牌id
       */
      @NotNull(message = "修改必须指定品牌id", groups = {UpdateGroup.class, UpdateGroup.class})
      @Null(message = "新增不能指定品牌id", groups = {SaveGroup.class})
      @TableId
      private Long brandId;
      /**
       * 品牌名
       *
       * @NotBlank注解不满足条件时的错误提示可以在ValidationMessages.properties文件中查看，
       * 或者在ValidationMessages_zh_CN.properties中查看中文版本的提示，
       * 如果也不满意的话，可以在注解中动态添加错误提示消息
       *
       */
      @NotBlank(message = "品牌名不能为空", groups = { UpdateGroup.class, SaveGroup.class })
      private String name;
      /**
       * 品牌logo地址
       */
      @NotBlank(message = "logo不能为空", groups = { UpdateGroup.class, SaveGroup.class })
      @URL(message = "logo必须是一个合法的URL地址", groups = { UpdateGroup.class, SaveGroup.class })
      private String logo;
      /**
       * 介绍
       */
      @NotBlank(groups = { UpdateGroup.class, SaveGroup.class })
      private String descript;
      /**
       * 显示状态[0-不显示；1-显示]
       *
       * 使用自定义注解，表示前端传来的数据只能为0或1
       *
       */
      @NotNull(groups = { UpdateGroup.class, SaveGroup.class })
      @AllowValues(value = {0, 1}, groups = { UpdateGroup.class, SaveGroup.class, UpdateStatusGroup.class })
      private Integer showStatus;
      /**
       * 检索首字母
       *
       * js中的正则为：/^[a-zA-Z]$/
       * 在java中，不需要前后的/
       */
      @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = { UpdateGroup.class, SaveGroup.class })
      private String firstLetter;
      /**
       * 排序
       */
      @Min(value = 0, message = "排序必须大于等于0", groups = { UpdateGroup.class, SaveGroup.class })
      @NotNull(groups = { UpdateGroup.class, SaveGroup.class })
      private Integer sort;
  }
  ```

  spring mvc在使用JSR303规范校验时，会获取groups参数中包含UpdateStatusGroup.class的值的注解，并对相应的字段进行校验。如上所述，只有brandId和showStatus这两个字段应用了UpdateStatusGroup，所以在校验时，只会最这两个属性进行校验。

