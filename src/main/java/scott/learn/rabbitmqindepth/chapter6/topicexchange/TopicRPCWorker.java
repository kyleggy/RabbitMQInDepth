package scott.learn.rabbitmqindepth.chapter6.topicexchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import scott.learn.rabbitmqindepth.base.AbstractConnection;
import scott.learn.rabbitmqindepth.chapter6.fanoutexchange.PublishFanoutExchange;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static scott.learn.rabbitmqindepth.chapter6.topicexchange.PublishTopicExchange.REQUEST_ROUTING_KEY;
import static scott.learn.rabbitmqindepth.chapter6.topicexchange.PublishTopicExchange.RESPONSE_ROUTING_KEY;

public class TopicRPCWorker extends AbstractConnection {

    public static void main(String[] args) throws IOException, TimeoutException {

        initialize();

        String requestQueueName = "rpc-worker-" + new Random().nextInt();

        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(requestQueueName, false, true, true, null);
        //check queue created

        if (declareOk.getQueue().equals(requestQueueName)) {
            System.out.println("RPC Response worker queue: " + requestQueueName + " was created successfully");
        }

        AMQP.Queue.BindOk bindOk = channel.queueBind(requestQueueName, PublishTopicExchange.TOPIC_RPC_REQUEST_EXCHANGE, "scott.rpc.new.*");
        if (bindOk != null) {
            System.out.println(requestQueueName + " was bind to the exchange: " + PublishTopicExchange.TOPIC_RPC_REQUEST_EXCHANGE + " with routing key: " + REQUEST_ROUTING_KEY);
        }

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(1300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //properties.getTimestamp()
                long seconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - properties.getTimestamp().getTime());
                System.out.println("Received TOPIC RPC request published " + seconds + " seconds ago");

                String message = new String(body, "UTF-8");
                System.out.println("Processing message: " + message);

                Map<String,Object> headers = new HashMap<String, Object>();
                headers.put("first_publish", properties.getTimestamp());
                // Build response properties including the timestamp from the first publish
                AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().
                        contentType(properties.getContentType()).correlationId(properties.getCorrelationId()).appId("Chapter 6 Listing 2 Consumer").
                        headers(headers).build();

                //Publish the response response
                channel.basicPublish(PublishTopicExchange.TOPIC_RPC_RESPONSE_EXCHANGE, RESPONSE_ROUTING_KEY, basicProperties, message.getBytes());

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(requestQueueName, false, consumer);


    }
}
