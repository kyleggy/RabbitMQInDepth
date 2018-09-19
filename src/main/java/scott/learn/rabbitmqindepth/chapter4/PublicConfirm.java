package scott.learn.rabbitmqindepth.chapter4;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PublicConfirm {
    private static final String exchange = "scott.public.confirm.go";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //Publisher confirm
        channel.confirmSelect();
        channel.addConfirmListener(new ConfirmCallback() {
            public void handle(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Ack call back");
            }
        }, new ConfirmCallback() {
            public void handle(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("Nack Call back");
            }
        });
        //It must declare the exchange to let publisher confirm work.
        channel.exchangeDeclare(exchange, BuiltinExchangeType.DIRECT);
        //channel.queueDeclare("hi public confirm", true, true, false, null);
        channel.basicPublish(exchange, "", null, "Public Confirm".getBytes());
    }

}
