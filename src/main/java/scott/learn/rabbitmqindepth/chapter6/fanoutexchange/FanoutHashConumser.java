package scott.learn.rabbitmqindepth.chapter6.fanoutexchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class FanoutHashConumser extends AbstractConnection {
    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        String queueName = "rpc-hash-consumer" + new Random().nextInt();

        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(queueName, false, true, true, null);
        //check queue created

        if (declareOk.getQueue().equals(queueName)) {
            System.out.println("RPC Response worker queue: " + queueName + " was created successfully");
        }

        AMQP.Queue.BindOk bindOk = channel.queueBind(queueName, PublishFanoutExchange.FANOUT_RPC_REQUESTS_EXCHANGE, "");
        if (bindOk != null) {
            System.out.println(queueName + " was bind to the exchange: " + PublishFanoutExchange.FANOUT_RPC_REQUESTS_EXCHANGE + " without routing key");
        }

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Image with correlation-id of " + properties.getCorrelationId() + " has a content type of " + properties.getContentType());

                channel.basicAck(envelope.getDeliveryTag(), false);
            }

        };
        channel.basicConsume(queueName, false, consumer);
    }
}
