package scott.learn.rabbitmqindepth.chapter6.directexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class PublishDirectExchange extends AbstractConnection {
    public static final String DIRECT_RPC_REQUESTS_EXCHANGE = "scott-direct-rpc-requests";
    public static final String DIRECT_RPC_REQUESTS_ROUTE_KEY = "detect-faces";
    public static final String RPC_RESPONSE_EXCHANGE = "scott-direct-rpc-replies";
    public static final String RPC_RESPONSE_ROUTE_KEY = "rpc-replies-route-key";
    public static final String IMAGE_ONE = "image_one";
    public static final String IMAGE_TWO = "image_two";
    public static final String IMAGE_THREE = "image_three";


    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        List<String> exchanges = new ArrayList<String>();
        exchanges.add(DIRECT_RPC_REQUESTS_EXCHANGE);
        exchanges.add(RPC_RESPONSE_EXCHANGE);

        for (String exchange: exchanges) {
            channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
        }
        channel.close();
        connection.close();

    }


}
