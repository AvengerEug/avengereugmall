package com.avengereug.mall.order;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {

    @Autowired
    AmqpAdmin amqpAdmin;


    @Test
    public void createExchange() {
        Exchange fanoutExchange = ExchangeBuilder.fanoutExchange("testFanoutExchange").build();
        amqpAdmin.declareExchange(fanoutExchange);
    }

}
