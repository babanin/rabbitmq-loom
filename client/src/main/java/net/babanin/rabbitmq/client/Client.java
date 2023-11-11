package net.babanin.rabbitmq.client;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Client {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            MathOperationsService mathOperationsService = FluentClientFactory.createClient(MathOperationsService.class, channel);
            IntBinaryOperationResponse response = mathOperationsService.sumNumbers(new IntBinaryOperationRequest(2, 3));

            System.out.println(response.value());
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
