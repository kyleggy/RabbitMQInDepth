package scott.learn.rabbitmqindepth.chapter4;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

public class MandatoryFailure {
    private static final String exchange = "MandatoryFailure";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.contentType("text/plain");
        builder.timestamp(new Date());
        String message = "server.cpu.utilization 25.5 1350884514pp";
        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT, false, false, null);

        try{
            channel.basicPublish(exchange, "", true, builder.build(), message.getBytes());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

        channel.addReturnListener(new HandlingReturnListener());
    }

}

class HandlingReturnListener implements ReturnListener {

    public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
        System.out.println(replyText + ":" + exchange + ":" +  body.toString());
    }
}
