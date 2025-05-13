package com.dx1ngy.trading.engine.controller;

import com.dx1ngy.trading.common.bean.Order;
import com.dx1ngy.trading.common.bean.OrderBook;
import com.dx1ngy.trading.engine.manager.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@RestController
public class TradeEngineController {

    private final TradingEngine tradingEngine;
    private final MessageManager messageManager;
    private final UserManager userManager;
    private final OrderManager orderManager;
    private final MatchEngineManager matchEngineManager;

    public TradeEngineController(TradingEngine tradingEngine, MessageManager messageManager,
                                 UserManager userManager, OrderManager orderManager,
                                 MatchEngineManager matchEngineManager) {
        this.tradingEngine = tradingEngine;
        this.messageManager = messageManager;
        this.userManager = userManager;
        this.orderManager = orderManager;
        this.matchEngineManager = matchEngineManager;
    }

    @GetMapping("/memory")
    public Map<String, Object> memory() {
        var map = new HashMap<String, Object>();
        map.put("lastOffset", tradingEngine.getLastOffset());
        map.put("dealId", MatchEngine.getDealId());
        map.put("orderId", messageManager.getOrderId());
        map.put("count", tradingEngine.getCount());
        map.put("userMap", userManager.getUserMap());
        map.put("userOrderMap", userManager.getUserOrderMap());
        map.put("userPositionMap", userManager.getUserPositonMap());
        map.put("orderMap", orderManager.getOrderMap());
        map.put("goodsPrice", orderManager.getGoodsPriceMap());
        map.put("matchEngineMap", matchEngineManager.getMatchEngineMap());
        return map;
    }

    @PostMapping("/user-info")
    public Map<String, Object> userInfo(@RequestBody Map<String, Long> req) {
        Long userId = req.get("userId");
        Long goodsId = req.get("goodsId");
        var user = userManager.getUserMap().get(userId);
        var map = new HashMap<String, Object>();
        map.put("money", user.getNum());
        map.put("margin", user.getMarginMap().getOrDefault(goodsId, BigDecimal.ZERO));
        map.put("gainLoss", user.getGainLossMap().getOrDefault(goodsId, BigDecimal.ZERO));
        Position positon0 = userManager.getPositon(userId, goodsId, 0);
        map.put("longNum", positon0 != null ? positon0.getNum() : BigDecimal.ZERO);
        Position positon2 = userManager.getPositon(userId, goodsId, 2);
        map.put("shortNum", positon2 != null ? positon2.getNum() : BigDecimal.ZERO);
        return map;
    }

    @PostMapping("/user-order-list")
    public List<Order> userOrderList(@RequestBody Map<String, Long> req) {
        Long userId = req.get("userId");
        Long goodsId = req.get("goodsId");
        return userManager.getOrderList(userId, goodsId, 10);
    }

    @PostMapping("/user-order-book")
    public Map<String, Object> userOrderBook(@RequestBody Map<String, Long> req) {
        var result = new HashMap<String, Object>();
        var buyList = new ArrayList<OrderBook>();
        var sellList = new ArrayList<OrderBook>();
        Long goodsId = req.get("goodsId");
        if (matchEngineManager.getMatchEngineMap().containsKey(goodsId)) {
            var matchEngine = matchEngineManager.getMatchEngineMap().get(goodsId);
            var buyBookMap = matchEngine.getBuyBookMap();
            var sellBookMap = matchEngine.getSellBookMap();
            buyList = getOrderBookList(buyBookMap, 3);
            sellList = getOrderBookList(sellBookMap, 3);
        }
        result.put("buyList", buyList);
        result.put("sellList", sellList);
        return result;
    }

    @PostMapping("/goods-price")
    public Map<String, Object> goodsPrice(@RequestBody Map<String, Long> req) {
        Long goodsId = req.get("goodsId");
        var map = new HashMap<String, Object>();
        map.put("goodsId", goodsId);
        map.put("price", orderManager.getGoodsPriceMap().get(goodsId));
        return map;
    }

    private ArrayList<OrderBook> getOrderBookList(ConcurrentSkipListMap<BigDecimal, BigDecimal> orderBookMap, int size) {
        var orderBookList = new ArrayList<OrderBook>();
        for (Map.Entry<BigDecimal, BigDecimal> entry : orderBookMap.entrySet()) {
            var orderBook = new OrderBook();
            orderBook.setPrice(entry.getKey());
            orderBook.setNum(entry.getValue());
            orderBook.setTotal(entry.getKey().multiply(entry.getValue()));
            orderBookList.add(orderBook);
            if (orderBookList.size() == size) {
                break;
            }
        }
        return orderBookList;
    }
}
