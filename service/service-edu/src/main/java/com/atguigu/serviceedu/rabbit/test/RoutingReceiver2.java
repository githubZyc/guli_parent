package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 **
 * 路由模式收到消息
 * @author ZYC
 * @date   2022/8/23 上午9:46
 */
public class RoutingReceiver2 {
    private final static String QUEUE_NAME = "queue_routing";
    private final static String EXCHANGE_NAME = "exchange_direct";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionFactoryUtil.connection();
        Channel channel = connection.createChannel();

        /**
         * 1. queue： 队列的名称 ；
         * 2. durable： 是否持久化 ；
         *
         * 当durable = false时，队列非持久化。因为队列是存放在内存中的，所以当RabbitMQ重启或者服务器重启时该队列就会丢失 ；
         * 当durable = true时，队列持久化。当RabbitMQ重启后队列不会丢失。RabbitMQ退出时它会将队列信息保存到 Erlang自带的Mnesia数据库 中，当RabbitMQ重启之后会读取该数据库 ；
         *
         * 3. exclusive： 是否排外的 ；
         * 当exclusive = true则设置队列为排他的。如果一个队列被声明为排他队列，该队列 仅对首次声明它的连接（Connection）可见，是该Connection私有的，类似于加锁，并在连接断开connection.close()时自动删除 ；
         * 当exclusive = false则设置队列为非排他的，此时不同连接（Connection）的管道Channel可以使用该队列 ；
         *     注意2点：
         * 排他队列是 基于连接(Connection) 可见的，同个连接（Connection）的不同管道 (Channel) 是可以同时访问同一连接创建的排他队列 。其他连接是访问不了的 ，强制访问将报错：com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=405, reply-text=RESOURCE_LOCKED - cannot obtain exclusive access to locked queue 'hello-testExclusice' in vhost '/'.；以下声明是没问题的：
         * 	Channel channel = connection.createChannel();
         *     Channel channel2 = connection.createChannel();
         *     channel.queueDeclare(QUEUE_NAME, false, true, false, null);
         *     channel2.queueDeclare(QUEUE_NAME, false, true, false, null);
         *
         * 	=》如果是不同的 connection 创建的 channel 和 channel2,那么以上的
         * 	=》channel2.queueDeclare()是会报错的!!!!!!
         *
         * "首次" 是指如果某个连接（Connection）已经声明了排他队列，其他连接是不允许建立同名的排他队列的。这个与普通队列不同：即使该队列是持久化的(durable = true)，一旦连接关闭或者客户端退出，该排他队列都会被自动删除，这种队列适用于一个客户端同时发送和读取消息的应用场景。
         * 4. autoDelete： 是否自动删除 ；如果autoDelete = true，当所有消费者都与这个队列断开连接时，这个队列会自动删除。注意： 不是说该队列没有消费者连接时该队列就会自动删除，因为当生产者声明了该队列且没有消费者连接消费时，该队列是不会自动删除的。
         * 5. arguments： 设置队列的其他一些参数，如 x-rnessage-ttl 、x-expires 、x-rnax-length 、x-rnax-length-bytes、 x-dead-letter-exchange、 x-deadletter-routing-key 、 x-rnax-priority 等。
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);

        //指定路由的key, 接收key 和 key2
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"key");
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"key2");

        /**
         * 该方法的作用是：进行消费端的限流
         * param1：prefetchSize，消息本身的大小 如果设置为0 那么表示对消息本身的大小不限制
         * param2：prefetchCount，告诉rabbitmq不要一次性给消费者推送大于N个消息
         * param3：global，是否将上面的设置应用于整个通道
         * false：表示只应用于当前消费者
         * true：表示当前通道的所有消费者都应用这个限流策略
         */
        channel.basicQos(0,1,false);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
        });
    }
}
