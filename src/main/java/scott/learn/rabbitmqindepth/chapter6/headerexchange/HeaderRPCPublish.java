package scott.learn.rabbitmqindepth.chapter6.headerexchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.GetResponse;
import scott.learn.rabbitmqindepth.base.AbstractConnection;
import scott.learn.rabbitmqindepth.chapter6.directexchange.PublishDirectExchange;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HeaderRPCPublish extends AbstractConnection {

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        String reponseQueueName = "response-header-queue-" + new Random().nextInt();
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(reponseQueueName, false, true, true, null);
        //check queue created
        if (declareOk.getQueue().equals(reponseQueueName)) {
            System.out.println("Response header queue: " + reponseQueueName + " was created successfully");
        }


        Map<String,Object> responseArguments = new HashMap<String, Object>();
        responseArguments.put("x-match", "all");
        responseArguments.put("source", "profile");
        responseArguments.put("object", "image");
        responseArguments.put("action", "reply");

        AMQP.Queue.BindOk bindOk = channel.queueBind(reponseQueueName, PublishHeaderExchange.HEADER_RPC_RESPONSE_EXCHANGE, "123", responseArguments);
        if (bindOk != null) {
            System.out.println(reponseQueueName + " was bind to the exchange:" + PublishHeaderExchange.HEADER_RPC_RESPONSE_EXCHANGE);
        }

        List<String> images = mockImages();


        for (int i = 0; i < images.size(); i ++) {
            Map<String,Object> requestHeaders = new HashMap<String, Object>();
            requestHeaders.put("source", "profile");
            requestHeaders.put("object", "image");
            requestHeaders.put("action", "new");
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().contentType("text/plain").correlationId(images.get(i))
                    //replyto is not a must:The reply-to property can be used to carry the
                    //routing key a consumer should use when replying
                    //to a message implementing an RPC pattern.
                    .replyTo(reponseQueueName)
                    //time stamp must be defined, otherwise it is null while reading
                    .timestamp(new Date()).headers(requestHeaders).build();
            channel.basicPublish(PublishHeaderExchange.HEADER_RPC_REQUEST_EXCHANGE, "", basicProperties, images.get(i).getBytes("UTF-8"));
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

