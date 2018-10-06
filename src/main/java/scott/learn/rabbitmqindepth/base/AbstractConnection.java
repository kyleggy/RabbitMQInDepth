package scott.learn.rabbitmqindepth.base;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class AbstractConnection {
    protected static Connection connection;
    protected static Channel channel;
    protected final static String HOST = "localhost";
    protected final static int PORT = 5682;

    protected static void initialize()throws IOException, TimeoutException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(PORT);
        connection = factory.newConnection();
        channel = connection.createChannel();
    }

}
