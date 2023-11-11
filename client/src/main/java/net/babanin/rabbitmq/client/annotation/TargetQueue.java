package net.babanin.rabbitmq.client.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface TargetQueue {
    String value();
}
