package scott.learn.rabbitmqindepth.chapter6.topicexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class PublishTopicExchange extends AbstractConnection {
    public static final String TOPIC_RPC_REQUEST_EXCHANGE = "scott-rpc-request-exchange";
    public static final String TOPIC_RPC_RESPONSE_EXCHANGE = "scott-rpc-response-exchange";
    public static final String REQUEST_ROUTING_KEY = "scott.rpc.new.request";
    public static final String RESPONSE_ROUTING_KEY = "scott.rpc.response";

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        List<String> exchanges = new ArrayList<String>();
        exchanges.add(TOPIC_RPC_REQUEST_EXCHANGE);
        exchanges.add(TOPIC_RPC_RESPONSE_EXCHANGE);

        for (String exchange: exchanges) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
        }

        channel.close();
        connection.close();
    }

}
