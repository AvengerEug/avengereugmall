package com.avengereug.mall.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


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

}
