package scott.learn.rabbitmqindepth.chapter6.exchangetoexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ExchangeBinding extends AbstractConnection {
    public static String CONSISTENT_HASH_EXCHANGE_TYPE = "x-consistent-hash";

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();
        channel.exchangeDeclare("distributed-events", CONSISTENT_HASH_EXCHANGE_TYPE);
        channel.exchangeDeclare("events", BuiltinExchangeType.TOPIC);
        channel.exchangeBind("events","distributed-events","#");


    }


}
