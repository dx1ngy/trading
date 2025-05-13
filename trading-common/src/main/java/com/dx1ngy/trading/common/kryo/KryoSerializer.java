package com.dx1ngy.trading.common.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;

public class KryoSerializer<T> implements Serializer<T> {

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(KryoFactory::create);

    @Override
    public byte[] serialize(String topic, T data) {
        var stream = new ByteArrayOutputStream();
        var output = new Output(stream);
        kryoThreadLocal.get().writeClassAndObject(output, data);
        output.close();
        return stream.toByteArray();
    }
}
