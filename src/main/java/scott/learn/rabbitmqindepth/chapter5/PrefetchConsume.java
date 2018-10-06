package scott.learn.rabbitmqindepth.chapter5;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PrefetchConsume extends AbstractConnection {
    private final static String QUEUE_NAME = "test-message";
    private final static String EXCHANGE_NAME = "test-message-exchange";
    private final static String GO_MESSAGE = "go";
    private final static String STOP_MESSAGE = "stop";
    private final static int PREFETCH_COUNT = 10;
    private static int unacknowledged = 0;

    public static void main(String[] argv) throws IOException, TimeoutException {
        channel.basicQos(PREFETCH_COUNT);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");



    }
}
