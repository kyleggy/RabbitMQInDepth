package scott.learn.rabbitmqindepth.chapter5;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Publish2000Message extends AbstractConnection {
    private final static String QUEUE_NAME = "test--many-message";
    private final static String EXCHANGE_NAME = "test-message-many-exchange";
    private final static String GO_MESSAGE = "run";
    private final static String STOP_MESSAGE = "stop";

    public static void main(String[] argv) throws IOException, TimeoutException {
        initialize();
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");
        for (int i = 0; i < 2000; i ++) {
            channel.basicPublish(EXCHANGE_NAME, "", null, GO_MESSAGE.getBytes());
        }
        channel.basicPublish(EXCHANGE_NAME, "", null, STOP_MESSAGE.getBytes());
        channel.close();
        connection.close();
    }


}
