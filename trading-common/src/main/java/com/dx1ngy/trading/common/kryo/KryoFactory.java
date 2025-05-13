package com.dx1ngy.trading.common.kryo;

import com.dx1ngy.core.Result;
import com.dx1ngy.trading.common.bean.*;
import com.esotericsoftware.kryo.Kryo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

public class KryoFactory {

    public static Kryo create() {
        var kryo = new Kryo();
        kryo.register(BigDecimal.class, 9);
        kryo.register(LocalDateTime.class, 10);
        kryo.register(CopyOnWriteArrayList.class, 11);
        kryo.register(Message.class, 12);
        kryo.register(Result.class, 13);
        kryo.register(Tick.class, 14);
        kryo.register(Deal.class, 15);
        kryo.register(Order.class, 16);
        kryo.register(Goods.class, 17);
        kryo.register(Data0_2.class, 18);
        kryo.register(Data3_7.class, 19);
        kryo.register(Data99.class, 20);
        return kryo;
    }
}
