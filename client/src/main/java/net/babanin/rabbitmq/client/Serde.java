package net.babanin.rabbitmq.client;

public interface Serde<Req, Res> {
    Res deserialize(byte[] bytes);

    byte[] serialize(Req request);
}
