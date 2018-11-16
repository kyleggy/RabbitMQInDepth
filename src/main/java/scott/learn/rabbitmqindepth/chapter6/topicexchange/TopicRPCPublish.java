package scott.learn.rabbitmqindepth.chapter6.topicexchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.GetResponse;
import scott.learn.rabbitmqindepth.base.AbstractConnection;
import scott.learn.rabbitmqindepth.chapter6.directexchange.PublishDirectExchange;
import scott.learn.rabbitmqindepth.chapter6.fanoutexchange.PublishFanoutExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static scott.learn.rabbitmqindepth.chapter6.topicexchange.PublishTopicExchange.RESPONSE_ROUTING_KEY;

public class TopicRPCPublish extends AbstractConnection {

    public static void main(String[] args) throws IOException, TimeoutException {
        initialize();

        String reponseQueueName = "response-queue-" + new Random().nextInt();
        AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(reponseQueueName, false, true, true, null);
        //check queue created
        if (declareOk.getQueue().equals(reponseQueueName)) {
            System.out.println("Response queue: " + reponseQueueName + " was created successfully");
        }

        AMQP.Queue.BindOk bindOk = channel.queueBind(reponseQueueName, PublishTopicExchange.TOPIC_RPC_RESPONSE_EXCHANGE, "scott.rpc.*");
        if (bindOk != null) {
            System.out.println(reponseQueueName + " was bind to the Topic exchange:" + RESPONSE_ROUTING_KEY + " with routing key: " + RESPONSE_ROUTING_KEY);
        }

        List<String> images = mockImages();


        for (int i = 0; i < images.size(); i ++) {
            AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().contentType("text/plain").correlationId(images.get(i))
                    //replyto is not a must:The reply-to property can be used to carry the
                    //routing key a consumer should use when replying
                    //to a message implementing an RPC pattern.
//                    .replyTo(reponseQueueName)
                    //time stamp must be defined, otherwise it is null while reading
                    .timestamp(new Date()).build();
            channel.basicPublish(PublishTopicExchange.TOPIC_RPC_REQUEST_EXCHANGE, PublishTopicExchange.REQUEST_ROUTING_KEY, basicProperties, images.get(i).getBytes("UTF-8"));
            boolean shouldRun = true;
            GetResponse getResponse = null;
            while (shouldRun) {
                try {
                    Thread.sleep(1800);
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
//
    }
    //
    private static List<String> mockImages() {
        List<String> images = new ArrayList<String>();
        images.add(PublishDirectExchange.IMAGE_ONE);
        images.add(PublishDirectExchange.IMAGE_TWO);
        images.add(PublishDirectExchange.IMAGE_THREE);
        return images;
    }
}
