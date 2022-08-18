package com.atguigu.serviceedu.rabbit.test;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionFactoryUtil {
    /**
     **
     * 链接rabbit mq
     * @author ZYC
     * @date   2022/8/18 下午2:28 []
     * @return com.rabbitmq.client.Connection
     */
    public static Connection connection() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        return connectionFactory.newConnection();
    }
}
