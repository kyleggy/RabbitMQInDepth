package scott.learn.rabbitmqindepth.chapter5.dlx;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class U5P3P3SpecifiyDeadLetterExchange extends AbstractConnection {
    private final static String QUEUE_DEAD_NAME = "rejected-message-queue";
    private final static String EXCHANGE_DEAD_NAME = "rejected-messages-exchange";

    private final static String QUEUE_NAME = "normal-message";
    private final static String EXCHANGE_NAME = "normal-message-exchange";
    private final static String GO_MESSAGE = "go";
    private final static String STOP_MESSAGE = "stop";

    public static void main(String[] argv) throws IOException, TimeoutException {
        initialize();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

        channel.exchangeDeclare(EXCHANGE_DEAD_NAME, BuiltinExchangeType.FANOUT);

        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", EXCHANGE_DEAD_NAME);
        channel.queueDeclare(QUEUE_DEAD_NAME, true, false, false, null);
        channel.queueBind(QUEUE_DEAD_NAME, EXCHANGE_DEAD_NAME, "");

        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

        for (int i = 0; i < 10; i ++) {
            channel.basicPublish(EXCHANGE_NAME, "", null, GO_MESSAGE.getBytes());
        }
        channel.basicPublish(EXCHANGE_NAME, "", null, STOP_MESSAGE.getBytes());
        channel.close();
        connection.close();
    }
}
