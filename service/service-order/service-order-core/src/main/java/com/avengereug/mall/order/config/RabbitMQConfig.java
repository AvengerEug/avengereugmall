package com.avengereug.mall.order.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RabbitMQConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 由如下方法可知：rabbitmq的自动装配过程中会初始化RabbitTemplate的这么一个操作rabbitmq
     * 的bean，熟悉spring的知识点就能知道，构造方法中依赖的所有对象，都会尝试从spring容器中
     * 去寻找，并填充进去。因此，如下方法的所有参数最终都会从spring容器中去获取。
     * @see RabbitAutoConfiguration.RabbitTemplateConfiguration#rabbitTemplate(
     * org.springframework.boot.autoconfigure.amqp.RabbitProperties,
     * org.springframework.beans.factory.ObjectProvider,
     * org.springframework.beans.factory.ObjectProvider,
     * org.springframework.amqp.rabbit.connection.ConnectionFactory)
     *
     * 其中，最具有代表意义的是：ObjectProvider<MessageConverter> messageConverter参数
     * 在构造rabbitTemplate时，会依赖于ObjectProvider，而它是一个factoryBean，
     * 其内部维护了MessageConverter类型的bean。
     * 因此，我们只需要手动创建一个MessageConverter类型的bean即可设置RabbitTemplate的
     * 发送/接收消息的序列化方式了
     *
     */
    @Bean
    public MessageConverter messageConverter() {
        // 使用json序列化方式进行消息格式化
        return new Jackson2JsonMessageConverter();
    }


    @PostConstruct
    public void init() {
        // 1、此确认机制是消息发送到消息服务器的broker组件时的回调，能保证消息发送到消息服务器中去了
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

            /**
             * @param correlationData 当前消息的唯一标识，是在发送消息时指定的correlationData数据，其实内部最重要的就是id属性
             * @param ack 这个ack确认机制与有没有消费者无关，此ack为服务端告知客户端，表示服务端收到了客户端发送的消息
             * @param cause 是一个异常信息，字符串类型，若服务端没有收到客户端的消息，则会告知客户端失败原因。
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                LOGGER.info("消息发送到rabbit服务器了。correlationData：{}, ack: {}, cause: {}", correlationData, ack, cause);
            }
        });

        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {

            /**
             * 此returnCallback的回调比较特殊，当交换机将消息路由到队列这一个步骤失败了才会回调此方法
             *
             * 可以自己测试，发送一个消息，指定一个不存在的路由键导致消息路由失败，此时就会进入到此方法中，
             * 我们可以在
             *
             * @param message 原生消息类型
             * @param replyCode 失败的原因码
             * @param replyText  失败的原因
             * @param exchange 哪个交换机路由消息到队列失败了
             * @param routingKey 发送消息时指定的路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                LOGGER.info("消息从交换机路由到队列失败，失败码：{}, 失败原因：{}，交换机名称：{}，路由键：{}，消息：{}", replyCode, replyText, exchange, replyCode, message);
            }
        });

    }

}
