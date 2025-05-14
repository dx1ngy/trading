package com.dx1ngy.trading.engine.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.dx1ngy.trading.common.bean.*;
import com.dx1ngy.trading.engine.TradingEngineCode;
import com.dx1ngy.trading.engine.TradingEngineException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
@Component
public class UserManager {

    //userId:User
    private ConcurrentHashMap<Long, User> userMap = new ConcurrentHashMap<>();
    //userId:goodsId:orderId:Order
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, ConcurrentSkipListMap<Long, Order>>> userOrderMap = new ConcurrentHashMap<>();
    //userId:goodsId:type:Position
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, ConcurrentHashMap<Integer, Position>>> userPositonMap = new ConcurrentHashMap<>();

    private final OrderManager orderManager;

    public UserManager(@Lazy OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public void addUser(Long userId) {
        if (userMap.containsKey(userId)) {
            throw new TradingEngineException(TradingEngineCode.ERR_4);
        }
        User user = new User();
        user.setUserId(userId);
        user.setNum(BigDecimal.ZERO);
        user.setMarginMap(new ConcurrentHashMap<>());
        user.setGainLossMap(new ConcurrentHashMap<>());
        userMap.put(userId, user);
    }

    public void addMoney(Long userId, BigDecimal num, boolean isCheck) {
        if (!userMap.containsKey(userId)) {
            throw new TradingEngineException(TradingEngineCode.ERR_4);
        }
        if (isCheck && num.compareTo(BigDecimal.ZERO) < 0) {
            throw new TradingEngineException(TradingEngineCode.ERR_4);
        }
        var user = userMap.get(userId);
        user.setNum(user.getNum().add(num));
    }

    public void subMoney(Long userId, BigDecimal num) {
        if (!userMap.containsKey(userId)) {
            throw new TradingEngineException(TradingEngineCode.ERR_4);
        }
        if (num.compareTo(BigDecimal.ZERO) < 0) {
            throw new TradingEngineException(TradingEngineCode.ERR_4);
        }
        var user = userMap.get(userId);
        if (user.getNum().compareTo(num) < 0) {
            throw new TradingEngineException(TradingEngineCode.ERR_5);
        }
        user.setNum(user.getNum().subtract(num));
    }

    public void addOrder(Order order) {
        if (userOrderMap.containsKey(order.getUserId())) {
            var goodsOrderMap = userOrderMap.get(order.getUserId());
            if (goodsOrderMap.containsKey(order.getGoodsId())) {
                var orderMap = goodsOrderMap.get(order.getGoodsId());
                orderMap.put(order.getOrderId(), order);
            } else {
                var orderMap = new ConcurrentSkipListMap<Long, Order>(Comparator.reverseOrder());
                orderMap.put(order.getOrderId(), order);
                goodsOrderMap.put(order.getGoodsId(), orderMap);
            }
        } else {
            var goodsOrderMap = new ConcurrentHashMap<Long, ConcurrentSkipListMap<Long, Order>>();
            var orderMap = new ConcurrentSkipListMap<Long, Order>(Comparator.reverseOrder());
            orderMap.put(order.getOrderId(), order);
            goodsOrderMap.put(order.getGoodsId(), orderMap);
            userOrderMap.put(order.getUserId(), goodsOrderMap);
        }
    }

    public void removeOrder(Order order) {
        userOrderMap.get(order.getUserId()).get(order.getGoodsId()).remove(order.getOrderId());
    }

    public void addGoods(Goods goods) {
        if (userPositonMap.containsKey(goods.getUserId())) {
            var goodsIdTypePositionMap = userPositonMap.get(goods.getUserId());
            if (goodsIdTypePositionMap.containsKey(goods.getGoodsId())) {
                var typePositionMap = goodsIdTypePositionMap.get(goods.getGoodsId());
                if (typePositionMap.containsKey(goods.getType())) {
                    var position = typePositionMap.get(goods.getType());
                    addPosition(typePositionMap, position, goods);
                } else {
                    addPosition(typePositionMap, null, goods);
                }
            } else {
                var typePositionMap = new ConcurrentHashMap<Integer, Position>();
                addPosition(typePositionMap, null, goods);
                goodsIdTypePositionMap.put(goods.getGoodsId(), typePositionMap);
            }
        } else {
            var goodsIdTypePositionMap = new ConcurrentHashMap<Long, ConcurrentHashMap<Integer, Position>>();
            var typePositionMap = new ConcurrentHashMap<Integer, Position>();
            addPosition(typePositionMap, null, goods);
            goodsIdTypePositionMap.put(goods.getGoodsId(), typePositionMap);
            userPositonMap.put(goods.getUserId(), goodsIdTypePositionMap);
        }
    }

    private void addPosition(ConcurrentHashMap<Integer, Position> typePositionMap, Position position, Goods goods) {
        var goodsKey = new GoodsKey(goods.getDealPrice(), goods.getMargin());
        if (position == null) {
            position = new Position();
            var goodsMap = new ConcurrentSkipListMap<GoodsKey, Goods>((o1, o2) -> {
                if (o1.equals(o2)) {
                    return 0;
                }
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            });
            goodsMap.put(goodsKey, goods);
            position.setNum(goods.getDealNum());
            position.setGoodsMap(goodsMap);
            typePositionMap.put(goods.getType(), position);
        } else {
            position.setNum(position.getNum().add(goods.getDealNum()));
            var goodsMap = position.getGoodsMap();
            if (goodsMap.containsKey(goodsKey)) {
                var old = goodsMap.get(goodsKey);
                old.setDealNum(old.getDealNum().add(goods.getDealNum()));
            } else {
                goodsMap.put(goodsKey, goods);
            }
        }
    }

    public CopyOnWriteArrayList<Goods> removeGoods(Goods goods) {
        if (!userPositonMap.containsKey(goods.getUserId())) {
            throw new TradingEngineException(TradingEngineCode.ERR_8);
        }
        var goodsIdTypePositionMap = userPositonMap.get(goods.getUserId());
        if (!goodsIdTypePositionMap.containsKey(goods.getGoodsId())) {
            throw new TradingEngineException(TradingEngineCode.ERR_8);
        }
        var typePositionMap = goodsIdTypePositionMap.get(goods.getGoodsId());
        if (!typePositionMap.containsKey(goods.getType())) {
            throw new TradingEngineException(TradingEngineCode.ERR_8);
        }
        var position = typePositionMap.get(goods.getType());
        BigDecimal num = goods.getDealNum();
        if (position.getNum().compareTo(num) < 0) {
            throw new TradingEngineException(TradingEngineCode.ERR_8);
        }
        position.setNum(position.getNum().subtract(num));
        var list = new CopyOnWriteArrayList<Goods>();
        var iterator = position.getGoodsMap().entrySet().iterator();
        while (iterator.hasNext() && num.compareTo(BigDecimal.ZERO) > 0) {
            var element = iterator.next().getValue();
            if (element.getDealNum().compareTo(num) <= 0) {
                num = num.subtract(element.getDealNum());
                iterator.remove();
                list.add(element);
            } else {
                element.setDealNum(element.getDealNum().subtract(num));
                var dealGoods = new Goods();
                dealGoods.setGoodsId(element.getGoodsId());
                dealGoods.setUserId(element.getUserId());
                dealGoods.setType(element.getType());
                dealGoods.setDealPrice(element.getDealPrice());
                dealGoods.setDealNum(num);
                dealGoods.setMargin(element.getMargin());
                list.add(dealGoods);
                num = BigDecimal.ZERO;
            }
        }
        return list;
    }

    public List<Order> getOrderList(Long userId, Long goodsId, Integer limit) {
        if (userOrderMap.containsKey(userId)) {
            var goodsOrderMap = userOrderMap.get(userId);
            if (goodsOrderMap.containsKey(goodsId)) {
                var stream = goodsOrderMap.get(goodsId).values().stream();
                if (limit == null) {
                    return stream.toList();
                } else {
                    return stream.limit(limit).toList();
                }
            }
        }
        return new ArrayList<>();
    }

    public Position getPositon(Long userId, Long goodsId, Integer type) {
        if (userPositonMap.containsKey(userId)) {
            var goodsTypePositionMap = userPositonMap.get(userId);
            if (goodsTypePositionMap.containsKey(goodsId)) {
                var typePositionMap = goodsTypePositionMap.get(goodsId);
                if (typePositionMap.containsKey(type)) {
                    return typePositionMap.get(type);
                }
            }
        }
        return null;
    }

    public void updateMarginGainLoss(Long userId, Long goodsId, BigDecimal dealPrice) {
        //包含两部分 一部分是已经提交平仓订单的，一部分是未提交平仓订单的
        BigDecimal marginTotal = BigDecimal.ZERO;
        BigDecimal gainLossTotal = BigDecimal.ZERO;
        var orderList = getOrderList(userId, goodsId, null);
        if (CollectionUtil.isNotEmpty(orderList)) {
            for (Order order : orderList) {
                //平多或平空
                if (order.getType() == 1 || order.getType() == 3) {
                    List<Goods> goodsList = order.getGoodsList();
                    for (Goods goods : goodsList) {
                        marginTotal = marginTotal.add(orderManager.getMarginTotal(goods.getMargin(), goods.getDealNum()));
                        gainLossTotal = gainLossTotal.add(orderManager.getGainLossTotal(goods.getType(), dealPrice, goods.getDealPrice(), goods.getDealNum()));
                    }
                }
            }
        }
        //多仓持仓总保证金
        var position0 = getPositon(userId, goodsId, 0);
        if (position0 != null) {
            for (Goods goods : position0.getGoodsMap().values()) {
                marginTotal = marginTotal.add(orderManager.getMarginTotal(goods.getMargin(), goods.getDealNum()));
                gainLossTotal = gainLossTotal.add(orderManager.getGainLossTotal(goods.getType(), dealPrice, goods.getDealPrice(), goods.getDealNum()));
            }
        }
        //空仓持仓总保证金
        var position2 = getPositon(userId, goodsId, 2);
        if (position2 != null) {
            for (Goods goods : position2.getGoodsMap().values()) {
                marginTotal = marginTotal.add(orderManager.getMarginTotal(goods.getMargin(), goods.getDealNum()));
                gainLossTotal = gainLossTotal.add(orderManager.getGainLossTotal(goods.getType(), dealPrice, goods.getDealPrice(), goods.getDealNum()));
            }
        }
        userMap.get(userId).getMarginMap().put(goodsId, marginTotal);
        userMap.get(userId).getGainLossMap().put(goodsId, gainLossTotal);
    }

}
