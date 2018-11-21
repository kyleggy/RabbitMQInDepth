package scott.learn.rabbitmqindepth.chapter6.headerexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class PublishHeaderExchange extends AbstractConnection {
    public static final String HEADER_RPC_REQUEST_EXCHANGE = "scott-rpc-header-request-exchange";
    public static final String HEADER_RPC_RESPONSE_EXCHANGE = "scott-rpc-header-response-exchange";

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        List<String> exchanges = new ArrayList<String>();
        exchanges.add(HEADER_RPC_REQUEST_EXCHANGE);
        exchanges.add(HEADER_RPC_RESPONSE_EXCHANGE);

        for (String exchange: exchanges) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.HEADERS);
        }

        channel.close();
        connection.close();

    }

}
