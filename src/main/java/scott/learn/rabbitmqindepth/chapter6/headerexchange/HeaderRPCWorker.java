package scott.learn.rabbitmqindepth.chapter6.headerexchange;

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


public class HeaderRPCWorker extends AbstractConnection {

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();
        String queueName = "rpc-header-worker-" + new Random().nextInt();
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(queueName, false, true, true, null);
        //check queue created
        if (declareOk.getQueue().equals(queueName)) {
            System.out.println("RPC Response header worker queue: " + queueName + " was created successfully");
        }

        Map<String,Object> requestArguments = new HashMap<String, Object>();
        requestArguments.put("x-match", "all");
        requestArguments.put("source", "profile");
        requestArguments.put("object", "image");
        requestArguments.put("action", "new");
        AMQP.Queue.BindOk bindOk = channel.queueBind(queueName, PublishHeaderExchange.HEADER_RPC_REQUEST_EXCHANGE, "", requestArguments);
        if (bindOk != null) {
            System.out.println(queueName + " was bind to the exchange: " + PublishHeaderExchange.HEADER_RPC_REQUEST_EXCHANGE + " with x-match set to all. ");
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
                headers.put("source", "profile");
                headers.put("object", "image");
                headers.put("action", "reply");
                headers.put("first_publish", properties.getTimestamp());
                // Build response properties including the timestamp from the first publish
                AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().
                        contentType(properties.getContentType()).correlationId(properties.getCorrelationId()).appId("Chapter 6 Listing 2 Consumer").
                        headers(headers).build();

                //Publish the response response
                channel.basicPublish(PublishHeaderExchange.HEADER_RPC_RESPONSE_EXCHANGE, "", basicProperties, message.getBytes());

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(queueName, false, consumer);


    }
}

