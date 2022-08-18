package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 **
 * 工作队列模式（Work queues）
 *
 * 在多个消费者之间分配任务（竞争的消费者模式），一个生产者对应多个消费者，一般适用于执行资源密集型任务，单个消费者处理不过来，需要多个消费者进行处理
 * 应用场景： 一个订单的处理需要10s，有多个订单可以同时放到消息队列，然后让多个消费者同时处理，这样就是并行了，而不是单个消费者的串行情况
 * @author ZYC
 * @date   2022/8/18 下午2:23
 */
public class WorkQueue {
    /**
     工作模式 一个生产多个消费
     */
    private final static String QUEUE_NAME = "queue_work";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionFactoryUtil.connection();
        Channel channel = connection.createChannel();
        /**
         * 声明队列
         * String queue, 队列的名称
         *
         * boolean durable：设置是否持久化。为true则设置队列为持久化。持久化的队列会存盘，在服务器重启的时候可以保证不丢失相关信息
         *
         * boolean exclusive：设置是否排他。为true则设置对列为排他的。如果一个队列被声明为排他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除。
         * 这里需要注意的三点：排他队列是基于连接可见，同一个连接的不同信道是可以同时访问同一个连接创建的排他队列；”
         * 首次“是指如果一个连接已经声明了一个排他队列，其他连接是不允许建立同名的排他队列的，这个与普通队列不同；
         * 即使该队列是持久华东，一旦连接关闭或者客户端退出，该排他队列都会自动被删除，这种队列适用于一个客户端同事发送和读取消息的应用场景
         *
         * boolean autoDelete：设置是否自动删除。为true则设置队列为自动删除。
         * 自动删除的前提是：至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时，才会自动删除。
         * 不能把这个参数错误地理解为：”当连接到此队列的所有客户端断开时，这个队列自动删除“，因为生产者客户端创建这个队列，或者没有消费者客户端与这个队列连接时，都不会自动删除这个队列
         *
         * Map<String, Object> arguments：设置队列的其他一些参数，如x-message-ttl等
         * ————————————————
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        for (int i = 0; i < 100; i++) {
            String message = "work mode message" + i;
            /**
             * exchange:要将消息发送到的Exchange(交换器)
             * routingKey:路由Key
             * mandatory:true 如果mandatory标记被设置
             * immediate: true 如果immediate标记被设置，注意：RabbitMQ服务端不支持此标记
             * props:其它的一些属性，如：{@link MessageProperties.PERSISTENT_TEXT_PLAIN}
             * body:消息内容
             **/
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("[x] Sent '" + message + "'");
            Thread.sleep(i * 10);
        }

        channel.close();
        connection.close();
    }
}
