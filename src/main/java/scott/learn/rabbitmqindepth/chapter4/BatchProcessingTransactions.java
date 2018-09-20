package scott.learn.rabbitmqindepth.chapter4;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class BatchProcessingTransactions {
    private static final String exchange = "TransactionExchangeDe1Scott";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchange, BuiltinExchangeType.TOPIC);
        channel.queueDeclare("transaction queue1", true, false, false, null);
        channel.queueBind("transaction queue1", exchange, "transaction queue");
        channel.txSelect();

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().deliveryMode(2).contentType("text/plain").timestamp(new Date()).type("important").build();

        channel.basicPublish(exchange,"transaction queue", basicProperties, "message".getBytes());

        channel.txCommit();

    }
}
