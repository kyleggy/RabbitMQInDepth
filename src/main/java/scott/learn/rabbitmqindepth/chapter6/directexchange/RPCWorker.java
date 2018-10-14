package scott.learn.rabbitmqindepth.chapter6.directexchange;

import com.rabbitmq.client.*;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static scott.learn.rabbitmqindepth.chapter6.directexchange.PublishDirectExchange.*;

public class RPCWorker extends AbstractConnection {

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();
        String queueName = "rpc-worker-" + new Random().nextInt();
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(queueName, false, true, true, null);
        //check queue created
        if (declareOk.getQueue().equals(queueName)) {
            System.out.println("RPC Response worker queue: " + queueName + " was created successfully");
        }

        AMQP.Queue.BindOk bindOk = channel.queueBind(queueName, PublishDirectExchange.DIRECT_RPC_REQUESTS_EXCHANGE, DIRECT_RPC_REQUESTS_ROUTE_KEY);
        if (bindOk != null) {
            System.out.println(queueName + " was bind to the exchange: " + PublishDirectExchange.DIRECT_RPC_REQUESTS_EXCHANGE + " with routing key: " + DIRECT_RPC_REQUESTS_ROUTE_KEY);
        }

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //properties.getTimestamp()
                long seconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - properties.getTimestamp().getTime());
                System.out.println("Received RPC request published " + seconds + " seconds ago");

                String message = new String(body, "UTF-8");
                System.out.println("Processing message: " + message);


                Map<String,Object> headers = new HashMap<String, Object>();
                headers.put("first_publish", properties.getTimestamp());
                // Build response properties including the timestamp from the first publish
                AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().
                        contentType(properties.getContentType()).correlationId(properties.getCorrelationId()).appId("Chapter 6 Listing 2 Consumer").
                        headers(headers).build();

                //Publish the response response
                channel.basicPublish(RPC_RESPONSE_EXCHANGE, RPC_RESPONSE_ROUTE_KEY, basicProperties, message.getBytes());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(queueName, false, consumer);


    }
}
