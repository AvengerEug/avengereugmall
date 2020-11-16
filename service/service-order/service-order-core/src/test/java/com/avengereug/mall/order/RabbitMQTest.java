package com.avengereug.mall.order;

import com.avengereug.mall.order.config.RabbitMQConfig;
import com.avengereug.mall.order.params.QueryParams;
import lombok.Data;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;

@SpringBootTest
public class RabbitMQTest {

    @Autowired
    AmqpAdmin amqpAdmin;


    /**
     * 使用amqpAdmin创建exchange
     */
    @Test
    public void createExchange() {
        Exchange fanoutExchange = ExchangeBuilder.fanoutExchange("test.fanout.exchange").build();
        amqpAdmin.declareExchange(fanoutExchange);
    }

    /**
     * 使用amqpAdmin创建queue
     */
    @Test
    public void createQueue() {
        Queue queue = QueueBuilder.durable("test.fanout.queue").build();
        amqpAdmin.declareQueue(queue);
    }


    /**
     * 使用amqpAdmin创建binding，将队列和交换机做绑定
     */
    @Test
    public void createBinding() {
        Queue queue = QueueBuilder.durable("test.fanout.queue").build();
        Exchange fanoutExchange = ExchangeBuilder.fanoutExchange("test.fanout.exchange").build();
        Binding binding = BindingBuilder.bind(queue).to(fanoutExchange).with("test.fanout.exchange.routingKey").and(null);
        amqpAdmin.declareBinding(binding);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 使用rabbitTemplate发送一条消息(默认发送字符串)
     * 1、需要指定发送到哪一个exchange中
     * 2、其次再指定使用具体的哪一个routingKey，这样才能确定将消息发送到对应routingKey绑定的队列中
     * 3、若对应的队列中有对应的消费者，那么这个消费者就能正常的消费消息
     *
     */
    @Test
    public void sendStringToMessage() {

        rabbitTemplate.convertAndSend("test.fanout.exchange", "test.fanout.exchange.routingKey", "Hello RabbitMQ!");
    }

    /**
     * 发送自定义的参数至RabbitMQ。
     * 自定义一个参数对象：QueryParams
     *
     * 若使用如下方式创建了一个类型为MessageConverter的bean，那么我们就能修改rabbitTemplate对
     * 消息的发送和接收的系列化方式了
     * @see RabbitMQConfig#messageConverter()
     */
    @Test
    public void sendCustomizedParamsToMessage() {
        for (int i = 0; i < 10; i++) {
            QueryParams queryParams = new QueryParams();
            queryParams.setCurrentPage(1);
            queryParams.setPageSize(i);
            rabbitTemplate.convertAndSend("test.fanout.exchange", "test.fanout.exchange.routingKey", queryParams);
        }
    }


}
