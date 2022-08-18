package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 **
 * 做最简单的事情，一个生产者对应一个消费者，RabbitMQ相当于一个消息代理，负责将A的消息转发给B
 * 应用场景： 将发送的电子邮件放到消息队列，然后邮件服务在队列中获取邮件并发送给收件人
 *
 * @author ZYC
 * @date   2022/8/18 上午9:33
 */
public class SimpleQueueReceiver {
    private final static String QUEUE_NAME = "simple_queue";

    public static void main(String[] args) throws Exception {
        // 获取连接
        Connection connection = ConnectionFactoryUtil.connection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback -> {
        });
    }
}
