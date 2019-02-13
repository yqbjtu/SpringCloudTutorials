package com.yq.producer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.SendResult;
import java.nio.charset.Charset;

/**
 * Simple to Introduction
 * className: SyncProducer
 *
 * @author EricYang
 * @version 2019/1/29 15:03
 */
public class SyncProducer {
    private static final String host = ConnectionFactory.DEFAULT_HOST;
    private static final int port = ConnectionFactory.DEFAULT_AMQP_PORT;
    private static final String virtualHost = ConnectionFactory.DEFAULT_VHOST;
    private static final String username = ConnectionFactory.DEFAULT_USER;
    private static final String password = ConnectionFactory.DEFAULT_PASS;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
         // "guest"/"guest" by default, limited to localhost connections
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setHost(host);
        factory.setPort(port);

        Connection conn = factory.newConnection();
        Channel channel = conn.createChannel();

        AMQP.BasicProperties properties = null;
        String exchangeName = "name1";
        String routingKey = "key1.cc";
        String msg = "test data";

        try {
            channel.basicPublish(
                    exchangeName,
                    routingKey,
                    properties,
                    msg.getBytes(UTF8));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            channel.close();
            conn.close();
        }

        System.out.println("done");
    }
}

