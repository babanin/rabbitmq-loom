package net.babanin.rabbitmq.client.annotation;

import net.babanin.rabbitmq.client.Serde;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MethodSerde {
    Class<? extends Serde<?, ?>> value();
}
