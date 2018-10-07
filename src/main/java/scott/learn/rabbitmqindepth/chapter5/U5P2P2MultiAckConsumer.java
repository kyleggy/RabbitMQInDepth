package scott.learn.rabbitmqindepth.chapter5;

import com.rabbitmq.client.*;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class U5P2P2MultiAckConsumer extends AbstractConnection {
    private final static String QUEUE_NAME = "test--many-message";
    private final static String EXCHANGE_NAME = "test-message-many-exchange";
    private static int unacknowledged = 0;

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        channel.basicQos(2000);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                //System.out.println(" [x] Received '" + message + "'");
                //channel.basicAck(envelope.getDeliveryTag(), false);
                unacknowledged ++;
                if (unacknowledged == 1000) {
                    channel.basicAck(envelope.getDeliveryTag(), true);
                    unacknowledged = 0;
                    System.out.println(message + " acknowledged.");
                }
            }
        };
        channel.basicConsume(QUEUE_NAME, false, consumer);
//        channel.close();
//        connection.close();

    }
}
