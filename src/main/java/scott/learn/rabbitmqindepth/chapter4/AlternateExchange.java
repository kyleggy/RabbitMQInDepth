package scott.learn.rabbitmqindepth.chapter4;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AlternateExchange {
    private static final String ALTERNATE_EXCHANGE= "Scott AE";
    private static final String ORIGINAL_EXCHANGE = "Scott OR";
    private static final String ALTERNATE_QUEUE = "Alternate QUEUE Scott";
    private static final String ALERT_RK = "Alert Route Key Scott";

    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alternate-exchange", ALTERNATE_EXCHANGE);

        channel.exchangeDeclare(ORIGINAL_EXCHANGE, BuiltinExchangeType.TOPIC, false, false, map);

        channel.exchangeDeclare(ALTERNATE_EXCHANGE, BuiltinExchangeType.FANOUT);

        channel.queueDeclare(ALTERNATE_QUEUE, true, false, false, null);

        channel.queueBind(ALTERNATE_QUEUE, ALTERNATE_EXCHANGE, ALERT_RK);

    }
}
