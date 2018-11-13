package scott.learn.rabbitmqindepth.chapter6.fanoutexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class PublishFanoutExchange extends AbstractConnection {
    public static final String FANOUT_RPC_REQUESTS_EXCHANGE = "scott-fanout-rpc-requests";
    public static final String FANOUT_RPC_RESPONSE_EXCHANGE = "scott-fanout-rpc-replies";

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        List<String> exchanges = new ArrayList<String>();
        exchanges.add(FANOUT_RPC_REQUESTS_EXCHANGE);
        exchanges.add(FANOUT_RPC_RESPONSE_EXCHANGE);

        for (String exchange: exchanges) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.FANOUT);
        }

        channel.close();
        connection.close();

    }

}
