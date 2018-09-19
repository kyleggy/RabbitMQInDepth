package scott.learn.rabbitmqindepth.chapter2;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PublishExample {
    private static final String exchange = "chapter2-example-scott";
    private static final String queue = "chapter2-example-scott-queue";
    private static final String routingKey = "chapter2example-scott-routing-key";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5672);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(queue, false, false,false, null);
        channel.queueBind(queue, exchange, routingKey);

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.contentType("text/plain");

        channel.basicPublish(exchange, routingKey, builder.build(), "Scott".getBytes());



    }

}
