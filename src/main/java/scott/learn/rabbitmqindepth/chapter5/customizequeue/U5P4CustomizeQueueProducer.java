package scott.learn.rabbitmqindepth.chapter5.customizequeue;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class U5P4CustomizeQueueProducer extends AbstractConnection {

    private final static String QUEUE_NAME = "customize-queue-message4";
    private final static String EXCHANGE_NAME = "customize-queue-message-exchange4";
    private final static String GO_MESSAGE = "go";
    private final static String STOP_MESSAGE = "stop";

    public static void main(String[] argv) throws IOException, TimeoutException {
        initialize();

        //x-expires
        Map<String, Object> arguments = new HashMap<String, Object>();

        //queue expire
        arguments.put("x-expires", 30000);
        //message expire
        arguments.put("x-message-ttl", 10000);

        //message max size
        arguments.put("x-max-length", 5);

        channel.queueDeclare(QUEUE_NAME, false, false, false, arguments);

        //Strange that fanout type exchange, the queue does not expire.
        //channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        for (int i = 0; i < 10; i ++) {
            channel.basicPublish(EXCHANGE_NAME, "", null, GO_MESSAGE.getBytes());
        }
        channel.basicPublish(EXCHANGE_NAME, "", null, STOP_MESSAGE.getBytes());
        channel.close();
        connection.close();
    }
}
