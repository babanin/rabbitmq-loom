package net.babanin.rabbitmq.client;

import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Channel;
import net.babanin.rabbitmq.client.annotation.MethodSerde;
import net.babanin.rabbitmq.client.annotation.ReplyQueue;
import net.babanin.rabbitmq.client.annotation.TargetQueue;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

public class FluentClientFactory {
    static <T> T createClient(Class<T> cls, Channel channel) {
        return (T) Proxy.newProxyInstance(FluentClientFactory.class.getClassLoader(), new Class[]{cls}, new FluentClientInvocationHandler<T>(cls, channel));
    }

    record MethodMetadata<Req, Res>(String targetQueue, String replyQueue, Class<Req> request, Class<Res> response, Serde<Req, Res> serde) {
    }

    static class FluentClientInvocationHandler<T> implements InvocationHandler {

        private final Class<T> cls;
        private final Channel channel;

        private Map<Method, MethodMetadata<?, ?>> metadata;

        public FluentClientInvocationHandler(Class<T> cls, Channel channel) {
            this.cls = cls;
            this.channel = channel;
            this.metadata = new HashMap<>();

            for (Method method : cls.getMethods()) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length > 1) {
                    throw new IllegalArgumentException("Class " + cls + " has method with more than one argument: " + method.getName());
                }

                Class<?> requestType = parameters[0].getType();
                Class<?> responseType = method.getReturnType();

                TargetQueue targetQueue = method.getAnnotation(TargetQueue.class);
                ReplyQueue replyQueue = method.getAnnotation(ReplyQueue.class);

                Serde<?, ?> serde;
                try {
                    MethodSerde methodSerde = method.getAnnotation(MethodSerde.class);
                    serde = methodSerde.value().getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
                
                this.metadata.put(method, new MethodMetadata(targetQueue.value(), replyQueue.value(), requestType, responseType, serde));

            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws IOException {
            assert args.length <= 1 : "Only one or zero arguments supported in fluent interface";
            Object request = args[0];

            MethodMetadata methodMetadata = metadata.get(method);
            
            BasicProperties props = new BasicProperties.Builder()
                    .replyTo(callbackQueueName)
                    .build();
            
            channel.queueDeclare(methodMetadata.targetQueue, false, false, false, null);
            channel.basicPublish("", methodMetadata.targetQueue, null, methodMetadata.serde.serialize(request));

            LockSupport.park();
            
            return null;
        }
    }
}
