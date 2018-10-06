package scott.learn.rabbitmqindepth.chapter5;

import com.rabbitmq.client.*;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BasicConsume extends AbstractConnection {
    private final static String QUEUE_NAME = "test-message";
    private final static String EXCHANGE_NAME = "test-message-exchange";
    private final static String GO_MESSAGE = "go";
    private final static String STOP_MESSAGE = "stop";
    private static boolean shouldRun = true;

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();
        //Set prefetch count
        channel.basicQos(10);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'" + " conumer tag: " + consumerTag + ".");

                if (message.equals(STOP_MESSAGE)) {
                    shouldRun = false;
                }
            }
        };
        while (shouldRun) {
            channel.basicConsume(QUEUE_NAME, true,consumer);
        }
        channel.close();
        connection.close();



    }
}
