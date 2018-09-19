package scott.learn.rabbitmqindepth.chapter4;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class SurviveHAQueues {
    private static final String exchange = "SurviveHAQueuesExchangeScott";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("x-ha-policy", "all");
        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, false, false, null);
        channel.queueDeclare("ha-all-queue", true, false, false, map);
        channel.queueBind("ha-all-queue", exchange, "");
        channel.basicPublish(exchange, "", null, "hi".getBytes());

    }
}
