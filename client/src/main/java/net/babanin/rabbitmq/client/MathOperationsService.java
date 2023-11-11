package net.babanin.rabbitmq.client;

import net.babanin.rabbitmq.client.annotation.MethodSerde;
import net.babanin.rabbitmq.client.annotation.ReplyQueue;
import net.babanin.rabbitmq.client.annotation.TargetQueue;

public interface MathOperationsService {
    @TargetQueue("multiply-numbers")
    @MethodSerde(IntBinaryOperationSerde.class)
    @ReplyQueue("multiply-numbers-response")
    IntBinaryOperationResponse multiplyNumbers(IntBinaryOperationRequest request);

    @TargetQueue("sum-numbers")
    @MethodSerde(IntBinaryOperationSerde.class)
    @ReplyQueue("sum-numbers-response")
    IntBinaryOperationResponse sumNumbers(IntBinaryOperationRequest request);
}
