package net.babanin.rabbitmq.client;

public class IntBinaryOperationSerde implements Serde<IntBinaryOperationRequest, IntBinaryOperationResponse> {
    @Override
    public IntBinaryOperationResponse deserialize(byte[] bytes) {
        assert bytes.length == 4 : "Expecting 4 bytes only in " + IntBinaryOperationSerde.class;
        
        int value = bytes[0] << 24 + bytes[1] << 16 + bytes[2] << 8 + bytes[3];
        return new IntBinaryOperationResponse(value);
    }

    @Override
    public byte[] serialize(IntBinaryOperationRequest request) {
        int a = request.a();
        int b = request.b();
        return new byte[]{
                (byte) (a >> 24 & 7),
                (byte) (a >> 16 & 7),
                (byte) (a >> 8 & 7),
                (byte) (a & 7),
                (byte) (b >> 24 & 7),
                (byte) (b >> 16 & 7),
                (byte) (b >> 8 & 7),
                (byte) (b & 7),
        };
    }
}
