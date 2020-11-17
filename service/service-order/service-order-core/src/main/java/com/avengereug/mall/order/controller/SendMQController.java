package com.avengereug.mall.order.controller;

import com.avengereug.mall.common.constants.MQConstants;
import com.avengereug.mall.order.params.QueryParams;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sendMQ")
public class SendMQController {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${server.port}")
    private String port;

    @GetMapping("/index")
    public String send(@RequestParam(value = "count") Integer count) {
        String exchange = MQConstants.EX_TEST;

        for (int i = 0; i < count; i++) {
            QueryParams queryParams = new QueryParams();
            queryParams.setCurrentPage(i);
            queryParams.setPageSize(i);
            rabbitTemplate.convertAndSend(exchange, "", queryParams, new CorrelationData(UUID.randomUUID().toString()));
        }

        return "ok";
    }


    /**
     * 启动两个service-order实例
     * 同时往MQConstants.MQ_TEST队列生产消息，你会发现每个实例只能处理一个
     * 消息，同一个消息不会同时发送到多个消费者中去
     *
     * 结论：若一个队列有多个相同的监听者，同一个消息不会同时发送到多个消费者中去。只会发送
     * 到其中一个消费者去处理
     *
     * @param queryParams
     */
    @RabbitListener(queues = MQConstants.MQ_TEST)
    public void listener(QueryParams queryParams) throws InterruptedException {
        System.out.println("接收到消息：" + queryParams + " 当前服务端占用端口：" + port);
        System.out.println("处理消息中");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("消息处理完成");
    }

}
