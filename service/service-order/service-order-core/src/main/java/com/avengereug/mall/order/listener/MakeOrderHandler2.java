package com.avengereug.mall.order.listener;

import com.avengereug.mall.order.params.QueryParams;
import com.avengereug.mall.order.params.QueryParams2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 使用@RabbitListener和@RabbitHandler来组合使用，
 * 来处理多个监听者监听同一条队列并且队列中消息的参数类型不同的情况
 * 如下：
 * 若我们将发送消息时，分别发送了不同类型的消息对象QueryParam和QueryParam2。
 * 因为我们每一个消息消费者的方法签名上定义了参数名的类型，如果我们只用
 * @RabbitListener注解来处理这种情形的话，我们需要指定重复的队列，
 * 这种写法代码有点冗余，因此可以使用@RabbitListener和@RabbitHandler
 * 来处理。比如当前类的写法。
 *
 * 但实际上，我们的每个消息队列应该是职责单一的，最好是一条消息队列处理
 * 一种业务。这样的话，能简化、方便开发
 *
 */
//@RabbitListener(queues = "test.fanout.queue")
//@Component
public class MakeOrderHandler2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(MakeOrderHandler2.class);

    @RabbitHandler
    public void handler(QueryParams message) {
        LOGGER.info("接收到消息，消息内容：{}, 消息类型：{}", message, message.getClass());
    }

    @RabbitHandler
    public void handler(QueryParams2 message2) {
        LOGGER.info("接收到消息，消息内容：{}, 消息类型：{}", message2, message2.getClass());
    }
}
