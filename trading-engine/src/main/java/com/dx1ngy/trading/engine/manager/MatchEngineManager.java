package com.dx1ngy.trading.engine.manager;

import com.dx1ngy.trading.common.bean.Deal;
import com.dx1ngy.trading.common.bean.Order;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Component
public class MatchEngineManager {

    //goodsId:MatchEngine
    private final ConcurrentHashMap<Long, MatchEngine> matchEngineMap = new ConcurrentHashMap<>();

    public List<Deal> process(Order order) {
        List<Deal> deals;
        if (matchEngineMap.containsKey(order.getGoodsId())) {
            deals = matchEngineMap.get(order.getGoodsId()).add(order);
        } else {
            var matchEngine = new MatchEngine();
            matchEngineMap.put(order.getGoodsId(), matchEngine);
            deals = matchEngine.add(order);
        }
        return deals;
    }

    public void cancelOrder(Order order) {
        matchEngineMap.get(order.getGoodsId()).remove(order);
    }

    public void removeOrderBook(Order order) {
        matchEngineMap.get(order.getGoodsId()).removeOrderBook(order.getType(), order.getPrice(), order.getNum());
    }

}
