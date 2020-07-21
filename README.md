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
    * 具体参考官网: [nacos集成springcloud](https://nacos.io/zh-cn/docs/quick-start-spring-cloud.html)
    * Nacos控制台页面: http://host:port/nacos   **nacos/nacos**
    
2. 配置中心的使用：[参考官网详细链接：nacos-config使用手册](https://github.com/alibaba/spring-cloud-alibaba/wiki/Nacos-config)

    ps：nacos拥有全局开关、支持yaml文件(需要配置)、拥有namespace、group、dataId等特性。通常我们可以使用namespace + group的粒度来实现服务中环境的隔离(namespace来做服务之间的隔离，用group来做服务中环境的隔离)，或者使用profile的粒度来实现环境的隔离。

3. nacos与spring cloud版本之间的选择：[参考github中wiki详细说明](https://github.com/alibaba/spring-cloud-alibaba/wiki/版本说明)

