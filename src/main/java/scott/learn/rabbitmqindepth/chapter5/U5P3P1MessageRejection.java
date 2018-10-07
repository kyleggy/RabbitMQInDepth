package scott.learn.rabbitmqindepth.chapter5;

import com.rabbitmq.client.*;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class U5P3P1MessageRejection extends AbstractConnection {
    private final static String QUEUE_NAME = "test-message";
    private final static String EXCHANGE_NAME = "test-message-exchange";
    private final static String STOP_MESSAGE = "stop";
    private static boolean shouldRedelivered = true;
    private static int count = 0;

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        channel.basicQos(3);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");


                if (shouldRedelivered) {
                    channel.basicReject(envelope.getDeliveryTag(), true);
                    System.out.println(message + " will be redelivered");
                    shouldRedelivered = false;
                    count ++;
                } else {
                    System.out.println(" [x] count [" + count + "] + Received '" + message + "'");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                    count ++;
                }

            }
        };
        channel.basicConsume(QUEUE_NAME, false, consumer);
//        channel.close();
//        connection.close();
    }
}
