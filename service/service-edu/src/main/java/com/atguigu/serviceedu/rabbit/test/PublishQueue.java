package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 **
 * 订阅模式（Publish/Subscribe）
 * 一次向许多消费者发送消息，一个生产者发送的消息会被多个消费者收到，也就是将消息将广播到所有的消费者中
 * 使用场景:
 * 更新商品库存后需要通知多个缓存和多个数据库，这里的结构应该是：
 * 一个fanout类型交换机扇出两个消息队列，分别为缓存消息队列，数据库消息队列
 * 一个缓存消息队列对应着多个缓存消费者
 * 一个数据库消息队列对应着多个数据库消费者
 *
 * @author ZYC
 * @date   2022/8/18 下午3:21
 * @return
 */
public class PublishQueue {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionFactoryUtil.connection();
        Channel channel = connection.createChannel();
        /**
         * String exchange 交换机名称
         * BuiltinExchangeType type 交换机类型，常见的如fanout、direct、topic
         * boolean durable 设置是否持久化。durable设置true表示持久化，反之是不持久化。持久化可以将将换机存盘，在服务器重启时不会丢失相关信息
         * boolean autoDelete 设置是否自动删除。autoDelete设置为true则表示自动删除。自动删除的前提是至少有一个队列或者交换机与这个交换器绑定的队列或者交换器都与之解绑
         * boolean internal 设置是否内置的。如果设置为true，则表示是内置的交换器，客户端程序无法直接发送消息到这个交换器中，只能通过交换器路由到交换器这种方式
         * Map<String, Object> arguments 其他一些结构化参数，比如alternate-exchange
         */
        channel.exchangeDeclare(EXCHANGE_NAME,BuiltinExchangeType.FANOUT,false,false,false,null);

        String message = "publish subscribe message";
        /**
         * exchange:要将消息发送到的Exchange(交换器)
         * routingKey:路由Key
         * mandatory:true 如果mandatory标记被设置
         * immediate: true 如果immediate标记被设置，注意：RabbitMQ服务端不支持此标记
         * props:其它的一些属性，如：{@link MessageProperties.PERSISTENT_TEXT_PLAIN}
         * body:消息内容
         **/
        channel.basicPublish(EXCHANGE_NAME,"",true,true,null,message.getBytes(StandardCharsets.UTF_8));
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
