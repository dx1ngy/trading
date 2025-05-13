package com.dx1ngy.trading.engine.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.dx1ngy.trading.common.bean.Deal;
import com.dx1ngy.trading.common.bean.Goods;
import com.dx1ngy.trading.common.bean.Order;
import com.dx1ngy.trading.engine.TradingEngineCode;
import com.dx1ngy.trading.engine.TradingEngineException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class OrderManager {

    //orderId:Order
    @Getter
    @Setter
    private ConcurrentHashMap<Long, Order> orderMap = new ConcurrentHashMap<>();

    //goodsId:price
    @Getter
    @Setter
    private ConcurrentHashMap<Long, BigDecimal> goodsPriceMap = new ConcurrentHashMap<>();

    private final MatchEngineManager matchEngineManager;
    private final UserManager userManager;

    public OrderManager(MatchEngineManager matchEngineManager, UserManager userManager) {
        this.matchEngineManager = matchEngineManager;
        this.userManager = userManager;
    }

    public List<Deal> openLongShort(Order order) {
        //开仓
        open(order);
        //撮合
        var deals = matchEngineManager.process(order);
        //结算
        settle(deals);
        return deals;
    }

    public List<Deal> closeLongShort(Order order) {
        //平仓
        close(order);
        //撮合
        var deals = matchEngineManager.process(order);
        //结算
        settle(deals);
        return deals;
    }

    private void open(Order order) {
        //扣保证金和手续费
        BigDecimal total = getMarginAndFeeTotal(order.getMargin(), order.getFee(), order.getNum());
        userManager.subMoney(order.getUserId(), total);
        //添加开仓订单和商品
        orderMap.put(order.getOrderId(), order);
        userManager.addOrder(order);
    }

    private void close(Order order) {
        Integer type = null;
        if (order.getType() == 1) {//平多
            type = 0;
        } else if (order.getType() == 3) {//平空
            type = 2;
        }
        var goods = new Goods();
        goods.setGoodsId(order.getGoodsId());
        goods.setUserId(order.getUserId());
        goods.setType(type);
        goods.setDealNum(order.getNum());
        //扣库存
        var list = userManager.removeGoods(goods);
        order.setGoodsList(list);
        //添加平仓订单
        orderMap.put(order.getOrderId(), order);
        userManager.addOrder(order);
    }

    public void cancelOrder(Order order) {
        var fullOrder = orderMap.get(order.getOrderId());
        if (fullOrder == null) {
            throw new TradingEngineException(TradingEngineCode.ERR_7);
        }
        //校验订单和商品
        if (!fullOrder.getUserId().equals(order.getUserId()) || !fullOrder.getGoodsId().equals(order.getGoodsId())) {
            throw new TradingEngineException(TradingEngineCode.ERR_4);
        }
        //取消数量
        BigDecimal num = order.getNum();
        if (num.compareTo(BigDecimal.ZERO) <= 0 || fullOrder.getAvailableNum().compareTo(num) < 0) {
            throw new TradingEngineException(TradingEngineCode.ERR_6);
        }
        //扣可用数量
        fullOrder.setAvailableNum(fullOrder.getAvailableNum().subtract(num));
        //返还保证金手续费或者库存
        refund(fullOrder, num);
        //清理订单
        clearOrder(fullOrder, false);
        //清理orderBook
        order.setType(fullOrder.getType());
        order.setPrice(fullOrder.getPrice());
        matchEngineManager.removeOrderBook(order);
    }

    private void settle(List<Deal> deals) {
        if (CollectionUtil.isNotEmpty(deals)) {
            for (Deal deal : deals) {
                var buyOrder = deal.getBuyOrder();
                var sellOrder = deal.getSellOrder();
                //清理可用数量为0的订单
                clearOrder(buyOrder, true);
                clearOrder(sellOrder, true);
                if (buyOrder.getUserId().equals(sellOrder.getUserId())) {
                    //买卖是同一个人则不成交 并返还保证金手续费或者库存
                    refund(buyOrder, deal.getDealNum());
                    refund(sellOrder, deal.getDealNum());
                } else {
                    //更新当前价
                    goodsPriceMap.put(deal.getBuyOrder().getGoodsId(), deal.getDealPrice());
                    //结算
                    if (buyOrder.getType() == 0) {//开多
                        settleOpen(buyOrder, deal.getDealPrice(), deal.getDealNum());
                    } else if (buyOrder.getType() == 3) {//平空
                        settleClose(buyOrder, deal.getDealPrice(), deal.getDealNum());
                    }
                    if (sellOrder.getType() == 1) {//平多
                        settleClose(sellOrder, deal.getDealPrice(), deal.getDealNum());
                    } else if (sellOrder.getType() == 2) {//开空
                        settleOpen(sellOrder, deal.getDealPrice(), deal.getDealNum());
                    }
                }
            }
        }
    }

    private void settleOpen(Order order, BigDecimal dealPrice, BigDecimal dealNum) {
        //添加用户多仓空仓库存
        var goods = new Goods();
        goods.setGoodsId(order.getGoodsId());
        goods.setUserId((order.getUserId()));
        goods.setType(order.getType());
        goods.setDealPrice(dealPrice);
        goods.setDealNum(dealNum);
        goods.setMargin(order.getMargin());
        userManager.addGoods(goods);
    }

    private void settleClose(Order order, BigDecimal dealPrice, BigDecimal dealNum) {
        var goodsList = order.getGoodsList();
        //总盈亏 有可能为负数
        BigDecimal total = calculate(goodsList, dealPrice, dealNum);
        userManager.addMoney(order.getUserId(), total, false);
    }

    private BigDecimal calculate(CopyOnWriteArrayList<Goods> goodsList, BigDecimal dealPrice, BigDecimal dealNum) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < goodsList.size(); i++) {
            if (dealNum.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
            var element = goodsList.get(i);
            if (element.getDealNum().compareTo(dealNum) <= 0) {
                dealNum = dealNum.subtract(element.getDealNum());
                total = total.add(getMarginGainLoss(element, dealPrice, element.getDealNum()));
                goodsList.remove(i);
            } else {
                element.setDealNum(element.getDealNum().subtract(dealNum));
                total = total.add(getMarginGainLoss(element, dealPrice, dealNum));
                dealNum = BigDecimal.ZERO;
            }
        }
        return total;
    }

    private void clearOrder(Order order, boolean isSettle) {
        if (order.getAvailableNum().compareTo(BigDecimal.ZERO) == 0) {
            orderMap.remove(order.getOrderId());
            userManager.removeOrder(order);
            if (!isSettle) {
                matchEngineManager.cancelOrder(order);
            }
        }
    }

    private void refund(Order order, BigDecimal num) {
        if (order.getType() == 0 || order.getType() == 2) {//开仓单
            //返还保证金和手续费
            BigDecimal total = getMarginAndFeeTotal(order.getMargin(), order.getFee(), num);
            userManager.addMoney(order.getUserId(), total, false);
        } else if (order.getType() == 1 || order.getType() == 3) {//平仓单 返还库存
            var goodsList = order.getGoodsList();
            for (int i = 0; i < goodsList.size(); i++) {
                if (num.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
                var element = goodsList.get(i);
                if (element.getDealNum().compareTo(num) <= 0) {
                    num = num.subtract(element.getDealNum());
                    var goods = new Goods();
                    goods.setGoodsId(element.getGoodsId());
                    goods.setUserId(element.getUserId());
                    goods.setType(element.getType());
                    goods.setDealPrice(element.getDealPrice());
                    goods.setDealNum(element.getDealNum());
                    goods.setMargin(element.getMargin());
                    userManager.addGoods(goods);
                    goodsList.remove(i);
                } else {
                    element.setDealNum(element.getDealNum().subtract(num));
                    var goods = new Goods();
                    goods.setGoodsId(element.getGoodsId());
                    goods.setUserId(element.getUserId());
                    goods.setType(element.getType());
                    goods.setDealPrice(element.getDealPrice());
                    goods.setDealNum(num);
                    goods.setMargin(element.getMargin());
                    userManager.addGoods(goods);
                    num = BigDecimal.ZERO;
                }
            }
        }
    }

    private BigDecimal getMarginGainLoss(Goods goods, BigDecimal dealPrice, BigDecimal dealNum) {
        //保证金
        BigDecimal marginTotal = getMarginTotal(goods.getMargin(), dealNum);
        //盈亏
        BigDecimal gainLossTotal = getGainLossTotal(goods.getType(), dealPrice, goods.getDealPrice(), dealNum);
        return marginTotal.add(gainLossTotal);
    }

    private BigDecimal getMarginAndFeeTotal(BigDecimal margin, BigDecimal fee, BigDecimal num) {
        BigDecimal marginTotal = getMarginTotal(margin, num);
        //开仓和平仓的总手续费
        BigDecimal feeTotal = getFeeTotal(fee, num).multiply(new BigDecimal("2"));
        return marginTotal.add(feeTotal);
    }

    public BigDecimal getMarginTotal(BigDecimal margin, BigDecimal num) {
        return margin.multiply(num);
    }

    public BigDecimal getFeeTotal(BigDecimal fee, BigDecimal num) {
        return fee.multiply(num);
    }

    public BigDecimal getGainLossTotal(Integer type, BigDecimal dealPrice, BigDecimal openPrice, BigDecimal num) {
        if (type == 0) {//多仓
            return dealPrice.subtract(openPrice).multiply(num);
        } else {//空仓
            return openPrice.subtract(dealPrice).multiply(num);
        }
    }
}
