package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 **
 * 订阅获取发布的消息
 * @author ZYC
 * @date   2022/8/18 下午3:39
 */
public class PSubscribeReceiver2 {
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT,false,false,false,null);
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
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(EXCHANGE_NAME, false, false, false, null);
        String queueName = declareOk.getQueue();

        /**
         * queue: 队列名称
         * exchange: 交换器的名称
         * routingKey: 用来绑定队列和交换器的路由键;
         * argument: 定义绑定的一些参数。
         * 不仅可以将队列和交换器绑定起来，也可以将已经被绑定的队列和交换器进行解绑。具体方法可以参考如下(具体的参数解释可以参考前面的内容，这里不再赘述):
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"",null);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 订阅消息的回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received2 '" + message + "'");
        };

        // 消费者，有消息时出发订阅回调函数
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });

    }
}
