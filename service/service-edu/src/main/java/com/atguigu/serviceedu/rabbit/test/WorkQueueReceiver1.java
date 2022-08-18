package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 **
 * 工作模式消息消费1
 * @author ZYC
 * @date   2022/8/18 下午2:46
 */
public class WorkQueueReceiver1 {
    private final static String QUEUE_NAME = "queue_work";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionFactoryUtil.connection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        /**
         * 该方法的作用是：进行消费端的限流
         * param1：prefetchSize，消息本身的大小 如果设置为0 那么表示对消息本身的大小不限制
         * param2：prefetchCount，告诉rabbitmq不要一次性给消费者推送大于N个消息
         * param3：global，是否将上面的设置应用于整个通道
         * false：表示只应用于当前消费者
         * true：表示当前通道的所有消费者都应用这个限流策略
         */
        channel.basicQos(0,1,false);
        DeliverCallback deliverCallback = (consumerTag,delivery)->{
            System.out.println(consumerTag);
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag:客户端生成的一个消费者标识
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback:客户端生成的一个消费者标识
         * @return 与消费者关联的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true,"", deliverCallback, cancelCallback->{});
    }
}
