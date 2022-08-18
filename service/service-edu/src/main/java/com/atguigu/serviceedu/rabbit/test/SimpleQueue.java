package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 **
 * 简单模式（Hello World）
 * 做最简单的事情，一个生产者对应一个消费者，RabbitMQ相当于一个消息代理，负责将A的消息转发给B
 * 应用场景： 将发送的电子邮件放到消息队列，然后邮件服务在队列中获取邮件并发送给收件人
 *
 * @author ZYC
 * @date   2022/8/18 上午9:33
 */
public class SimpleQueue {
    private final static String QUEUE_NAME = "simple_queue";

    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionFactoryUtil.connection();
        Channel channel = connection.createChannel();

        // 声明队列
        // queue：队列名
        // durable：是否持久化
        // exclusive：是否排外  即只允许该channel访问该队列   一般等于true的话用于一个队列只能有一个消费者来消费的场景
        // autoDelete：是否自动删除  消费完删除
        // arguments：其他属性
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //消息内容
        String message = "hello hello";
        /**
         * exchange:要将消息发送到的Exchange(交换器)
         * routingKey:路由Key
         * mandatory:true 如果mandatory标记被设置
         * immediate: true 如果immediate标记被设置，注意：RabbitMQ服务端不支持此标记
         * props:其它的一些属性，如：{@link MessageProperties.PERSISTENT_TEXT_PLAIN}
         * body:消息内容
         **/
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("[x]Sent '" + message + "'");

        //最后关闭通关和连接
        channel.close();
        connection.close();

    }
}
