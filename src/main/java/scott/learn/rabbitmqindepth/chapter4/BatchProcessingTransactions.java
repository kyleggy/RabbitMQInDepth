package scott.learn.rabbitmqindepth.chapter4;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class BatchProcessingTransactions {
    private static final String exchange = "TransactionExchangeScott";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
        channel.queueDeclare("transaction queue", true, false, false, null);
        channel.queueBind("transaction queue", exchange, "transaction queue");
        channel.txSelect();

        channel.basicPublish(exchange,"transaction queue", MessageProperties.PERSISTENT_TEXT_PLAIN, "message".getBytes());

        channel.txCommit();

    }
}
