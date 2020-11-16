package com.avengereug.mall.order.listener;

import com.avengereug.mall.order.params.QueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class MakeOrderHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MakeOrderHandler.class);

    /**
     * 1、spring会自动将我们发送的消息类型转化成Message对象，
     * 它是原生的消息类型，其内部的body包含了我们自定义消息对象
     * @see org.springframework.amqp.core.Message
     *
     * 2、如果我们不需要原生的Message属性的话，我们可以直接使用自定义的对象来进行接收。
     *    spring会自动将字节数据序列化成我们指定的对象，
     *    原因就是：原生消息对象中，有一个叫__TypeId__的属性，指定了消息体的类型
     *
     * 3、我们也可以添加一个叫Channel类型的属性，可以获取到长链接中的通道
     *
     * 场景一：
     *   若当前服务是集群方式部署的，此时肯定会对同一个队列有多个消费者。
     *   但是，每一条消息有且只有一个消费者能够消费到
     * 场景二：
     *   只有消费者执行完逻辑后，才会继续接收后面的消息。
     *   如果消费者消费消息的逻辑执行了10s钟，那这个消费者要等10s钟以后才能继续消费新的消息
     *   并不能同时消费多条消息，也就是说，消费者的逻辑是同步的，有顺序的
     *
     * @RabbitHandler和@RabbitListener的区别
     * 查看如下类注释
     * @see MakeOrderHandler2
     *
     * @param message
     */
    @RabbitListener(queues = "test.fanout.queue")
    public void handler(QueryParams message) throws InterruptedException {
        LOGGER.info("接收到消息，消息内容：{}, 消息类型：{}", message, message.getClass());
        TimeUnit.SECONDS.sleep(3);
        LOGGER.info("消费消息完成：消息id：{}", message.getPageSize());
    }
}
