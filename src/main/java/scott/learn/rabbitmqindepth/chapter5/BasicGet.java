package scott.learn.rabbitmqindepth.chapter5;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.AMQImpl;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BasicGet {
    private final static String QUEUE_NAME = "test-message";
    private final static String EXCHANGE_NAME = "test-message-exchange";
    private final static String GO_MESSAGE = "go";
    private final static String STOP_MESSAGE = "stop";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        while (true) {
           String s =  new String(channel.basicGet(QUEUE_NAME, true).getBody());
           System.out.println(s);
           if (s.equals(STOP_MESSAGE)) {
               break;
           }
        }
        channel.close();
        connection.close();



    }
}
