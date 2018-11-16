package scott.learn.rabbitmqindepth.chapter6.topicexchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static scott.learn.rabbitmqindepth.chapter6.topicexchange.PublishTopicExchange.REQUEST_ROUTING_KEY;
import static scott.learn.rabbitmqindepth.chapter6.topicexchange.PublishTopicExchange.RESPONSE_ROUTING_KEY;

public class TopicScottSleep  extends AbstractConnection {

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        String scottSleepQueueName = "Scott-Sleep-Queue" + new Random().nextInt();

        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(scottSleepQueueName, false, true, true, null);

        if (declareOk.getQueue().equals(scottSleepQueueName)) {
            System.out.println("Scott Sleep queue: " + scottSleepQueueName + " was created successfully");
        }

        AMQP.Queue.BindOk bindOk = channel.queueBind(scottSleepQueueName, PublishTopicExchange.TOPIC_RPC_REQUEST_EXCHANGE, "scott.rpc.#");
        if (bindOk != null) {
            System.out.println(scottSleepQueueName + " was bind to the exchange: " + PublishTopicExchange.TOPIC_RPC_REQUEST_EXCHANGE + " with routing key: " + REQUEST_ROUTING_KEY);
        }

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("Start to Sleep " + message);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(scottSleepQueueName, false, consumer);


    }
}
