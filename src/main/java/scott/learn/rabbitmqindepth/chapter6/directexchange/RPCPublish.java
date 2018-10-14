package scott.learn.rabbitmqindepth.chapter6.directexchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.GetResponse;
import scott.learn.rabbitmqindepth.base.AbstractConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static scott.learn.rabbitmqindepth.chapter6.directexchange.PublishDirectExchange.DIRECT_RPC_REQUESTS_ROUTE_KEY;
import static scott.learn.rabbitmqindepth.chapter6.directexchange.PublishDirectExchange.RPC_RESPONSE_ROUTE_KEY;

public class RPCPublish extends AbstractConnection {

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        String reponseQueueName = "response-queue-" + new Random().nextInt();
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(reponseQueueName, false, true, true, null);
        //check queue created
        if (declareOk.getQueue().equals(reponseQueueName)) {
            System.out.println("Response queue: " + reponseQueueName + " was created successfully");
        }

        AMQP.Queue.BindOk bindOk = channel.queueBind(reponseQueueName, PublishDirectExchange.RPC_RESPONSE_EXCHANGE, RPC_RESPONSE_ROUTE_KEY);
        if (bindOk != null) {
            System.out.println(reponseQueueName + " was bind to the exchange:" + PublishDirectExchange.RPC_RESPONSE_EXCHANGE);
        }

        List<String> images = mockImages();


        for (int i = 0; i < images.size(); i ++) {
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().contentType("text/plain").correlationId(images.get(i)).replyTo(reponseQueueName)
                    //time stamp must be defined, otherwise it is null while reading
                    .timestamp(new Date()).build();
            channel.basicPublish(PublishDirectExchange.DIRECT_RPC_REQUESTS_EXCHANGE, DIRECT_RPC_REQUESTS_ROUTE_KEY, basicProperties, images.get(i).getBytes("UTF-8"));
            boolean shouldRun = true;
            GetResponse getResponse = null;
            while (shouldRun) {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getResponse = channel.basicGet(reponseQueueName, false);
                if (getResponse != null) {
                    shouldRun = false;
                }
            }
            if (getResponse != null) {
                channel.basicAck(getResponse.getEnvelope().getDeliveryTag(), false);
                AMQP.BasicProperties properties = getResponse.getProps();
                //Calculate how long it took from publish to response
                long seconds = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - ((Date)properties.getHeaders().get("first_publish")).getTime());
                // print('Facial detection RPC call for image %s total duration: %s' %
                //          (message.properties['correlation_id'], duration))
                System.out.println("Facial detection RPC call for image " + properties.getCorrelationId() + " total duration " + seconds);
            }



        }

        System.out.println("RPC requests processed");
        channel.close();
        connection.close();

    }

    private static List<String> mockImages() {
        List<String> images = new ArrayList<String>();
        images.add(PublishDirectExchange.IMAGE_ONE);
        images.add(PublishDirectExchange.IMAGE_TWO);
        images.add(PublishDirectExchange.IMAGE_THREE);
        return images;
    }
}
