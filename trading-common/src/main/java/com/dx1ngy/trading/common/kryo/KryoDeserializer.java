package com.dx1ngy.trading.common.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.apache.kafka.common.serialization.Deserializer;

public class KryoDeserializer<T> implements Deserializer<T> {

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(KryoFactory::create);

    @Override
    public T deserialize(String topic, byte[] data) {
        var input = new Input(data);
        var obj = (T) kryoThreadLocal.get().readClassAndObject(input);
        input.close();
        return obj;
    }
}
