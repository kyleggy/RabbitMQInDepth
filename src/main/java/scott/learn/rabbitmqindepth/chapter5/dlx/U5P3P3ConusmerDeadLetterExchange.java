package scott.learn.rabbitmqindepth.chapter5.dlx;

import com.rabbitmq.client.*;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class U5P3P3ConusmerDeadLetterExchange extends AbstractConnection {
    private final static String QUEUE_NAME = "normal-message";
    private final static String EXCHANGE_NAME = "normal-message-exchange";
    private final static String QUEUE_DEAD_NAME = "rejected-message-queue";
    private final static String EXCHANGE_DEAD_NAME = "rejected-messages-exchange";
    private final static String STOP_MESSAGE = "stop";
//    private static boolean shouldRedelivered = true;
//    private static int count = 0;

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        channel.exchangeDeclare(EXCHANGE_DEAD_NAME, BuiltinExchangeType.FANOUT);

        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", EXCHANGE_DEAD_NAME);
        channel.queueDeclare(QUEUE_DEAD_NAME, true, false, false, null);
        channel.queueBind(QUEUE_DEAD_NAME, EXCHANGE_DEAD_NAME, "");

        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                channel.basicReject(envelope.getDeliveryTag(), false);


            }
        };
        channel.basicConsume(QUEUE_NAME, false, consumer);
//        channel.close();
//        connection.close();
    }
}
