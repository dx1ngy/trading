package com.dx1ngy.trading.engine.manager;

import com.dx1ngy.trading.common.bean.Deal;
import com.dx1ngy.trading.common.bean.Order;
import com.dx1ngy.trading.common.bean.OrderKey;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MatchEngine {

    //多个引擎公用一个id
    @Getter
    @Setter
    private static volatile long dealId = 1;

    //价格正序，时间正序
    @Getter
    private final TreeMap<OrderKey, Order> sellMap = new TreeMap<>((o1, o2) -> {
        if (o1.price().compareTo(o2.price()) == 0) {
            return o1.orderId().compareTo(o2.orderId());
        } else {
            return o1.price().compareTo(o2.price());
        }
    });

    //价格倒序，时间正序
    @Getter
    private final TreeMap<OrderKey, Order> buyMap = new TreeMap<>((o1, o2) -> {
        if (o1.price().compareTo(o2.price()) == 0) {
            return o1.orderId().compareTo(o2.orderId());
        } else {
            return o2.price().compareTo(o1.price());
        }
    });

    //price:num
    @Getter
    private final ConcurrentSkipListMap<BigDecimal, BigDecimal> sellBookMap = new ConcurrentSkipListMap<>(Comparator.naturalOrder());
    //price:num
    @Getter
    private final ConcurrentSkipListMap<BigDecimal, BigDecimal> buyBookMap = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

    public List<Deal> add(Order order) {
        Integer type = order.getType();
        var orderKey = new OrderKey(order.getOrderId(), order.getPrice());
        if (type == 0 || type == 3) {//买方向
            buyMap.put(orderKey, order);
            buyBookMap.put(order.getPrice(), buyBookMap.getOrDefault(order.getPrice(), BigDecimal.ZERO).add(order.getNum()));
        } else {//卖方向
            sellMap.put(orderKey, order);
            sellBookMap.put(order.getPrice(), sellBookMap.getOrDefault(order.getPrice(), BigDecimal.ZERO).add(order.getNum()));
        }
        return match();
    }

    public void remove(Order order) {
        var orderKey = new OrderKey(order.getOrderId(), order.getPrice());
        if (order.getType() == 0 || order.getType() == 3) {//买方向
            buyMap.remove(orderKey);
        } else {//卖方向
            sellMap.remove(orderKey);
        }
    }

    public void removeOrderBook(Integer type, BigDecimal price, BigDecimal num) {
        if (type == 0 || type == 3) {//买方向
            BigDecimal subtract = buyBookMap.get(price).subtract(num);
            if (subtract.compareTo(BigDecimal.ZERO) == 0) {
                buyBookMap.remove(price);
            } else {
                buyBookMap.put(price, subtract);
            }
        } else {//卖方向
            BigDecimal subtract = sellBookMap.get(price).subtract(num);
            if (subtract.compareTo(BigDecimal.ZERO) == 0) {
                sellBookMap.remove(price);
            } else {
                sellBookMap.put(price, subtract);
            }
        }
    }

    private List<Deal> match() {
        var deals = new ArrayList<Deal>();
        var buyOrderEntry = buyMap.firstEntry();
        var sellOrderEntry = sellMap.firstEntry();
        while (buyOrderEntry != null && sellOrderEntry != null) {
            var buyOrder = buyOrderEntry.getValue();
            var sellOrder = sellOrderEntry.getValue();
            if (buyOrder.getPrice().compareTo(sellOrder.getPrice()) < 0) {
                break;
            }
            BigDecimal dealPrice = sellOrder.getPrice();
            BigDecimal dealNum;
            if (buyOrder.getAvailableNum().compareTo(sellOrder.getAvailableNum()) > 0) {
                dealNum = sellOrder.getAvailableNum();
                sellOrder.setAvailableNum(BigDecimal.ZERO);
                buyOrder.setAvailableNum(buyOrder.getAvailableNum().subtract(dealNum));
                sellMap.remove(sellOrderEntry.getKey());
                sellOrderEntry = sellMap.firstEntry();
            } else if (buyOrder.getAvailableNum().compareTo(sellOrder.getAvailableNum()) < 0) {
                dealNum = buyOrder.getAvailableNum();
                sellOrder.setAvailableNum(sellOrder.getAvailableNum().subtract(dealNum));
                buyOrder.setAvailableNum(BigDecimal.ZERO);
                buyMap.remove(buyOrderEntry.getKey());
                buyOrderEntry = buyMap.firstEntry();
            } else {
                dealNum = buyOrder.getAvailableNum();
                sellOrder.setAvailableNum(BigDecimal.ZERO);
                buyOrder.setAvailableNum(BigDecimal.ZERO);
                sellMap.remove(sellOrderEntry.getKey());
                buyMap.remove(buyOrderEntry.getKey());
                sellOrderEntry = sellMap.firstEntry();
                buyOrderEntry = buyMap.firstEntry();
            }
            removeOrderBook(buyOrder.getType(), buyOrder.getPrice(), dealNum);
            removeOrderBook(sellOrder.getType(), sellOrder.getPrice(), dealNum);
            var deal = new Deal();
            deal.setDealId(dealId++);
            deal.setDealPrice(dealPrice);
            deal.setDealNum(dealNum);
            deal.setBuyOrder(buyOrder);
            deal.setSellOrder(sellOrder);
            deal.setDealTime(LocalDateTime.now());
            deals.add(deal);
        }
        return deals;
    }
}
