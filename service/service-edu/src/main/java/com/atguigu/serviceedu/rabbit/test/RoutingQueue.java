package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 **
 * 路由模式
 * 路由模式 有选择地（Routing key）接收消息，发送消息到交换机并且要指定路由key,消费者将队列绑定到交换机时需要指定路由key，仅消费者指定路由key的消息
 * 应用场景：如在商品库中增加一台iphone12，iphone12促销活动消费者指定routing key为iphone12，
 * 只有此促销活动会接收到消息，其他促销活动不关心也不会消费此routing key的消息。
 * @author ZYC
 * @date   2022/8/22 上午9:08
 * @return
 */
public class RoutingQueue {
    private final static String EXCHANGE_NAME = "exchange_direct";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionFactoryUtil.connection();
        Channel channel = connection.createChannel();


        /**
         * 交换机声明
         * String exchange 交换机名称
         * BuiltinExchangeType type 交换机类型，常见的如fanout、direct、topic
         * boolean durable 设置是否持久化。durable设置true表示持久化，反之是不持久化。持久化可以将将换机存盘，在服务器重启时不会丢失相关信息
         * boolean autoDelete 设置是否自动删除。autoDelete设置为true则表示自动删除。自动删除的前提是至少有一个队列或者交换机与这个交换器绑定的队列或者交换器都与之解绑
         * boolean internal 设置是否内置的。如果设置为true，则表示是内置的交换器，客户端程序无法直接发送消息到这个交换器中，只能通过交换器路由到交换器这种方式
         * Map<String, Object> arguments 其他一些结构化参数，比如alternate-exchange
         */
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT,false,false,false,null);

        // 只有routingKey相同的才会消费
        String message = "routing mode message";
        /**
         * exchange:要将消息发送到的Exchange(交换器)
         * routingKey:路由Key
         * mandatory:true 如果mandatory标记被设置
         * immediate: true 如果immediate标记被设置，注意：RabbitMQ服务端不支持此标记
         * props:其它的一些属性，如：{@link MessageProperties.PERSISTENT_TEXT_PLAIN}
         * body:消息内容
         **/
        channel.basicPublish(EXCHANGE_NAME, "key2", true,true,null, message.getBytes());

        System.out.println("[x] Sent '" + message + "'");
        channel.close();
        connection.close();
    }
}
