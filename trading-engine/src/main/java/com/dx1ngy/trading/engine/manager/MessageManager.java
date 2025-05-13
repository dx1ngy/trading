package com.dx1ngy.trading.engine.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.dx1ngy.core.Results;
import com.dx1ngy.trading.common.bean.*;
import com.dx1ngy.trading.engine.TradingEngineException;
import com.dx1ngy.trading.engine.consumer.DealQueueHandler;
import com.dx1ngy.trading.engine.consumer.ResultQueueHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class MessageManager {

    @Getter
    @Setter
    private volatile long orderId = 1;

    private final UserManager userManager;
    private final OrderManager orderManager;
    private final ResultQueueHandler resultQueueHandler;
    private final DealQueueHandler dealQueueHandler;

    public MessageManager(UserManager userManager, OrderManager orderManager,
                          ResultQueueHandler resultQueueHandler, DealQueueHandler dealQueueHandler) {
        this.userManager = userManager;
        this.orderManager = orderManager;
        this.resultQueueHandler = resultQueueHandler;
        this.dealQueueHandler = dealQueueHandler;
    }

    public void dispatch(Message message) {
        try {
            List<Deal> deals = null;
            switch (message.getMessageType()) {
                case 0: {//注册
                    var data = (Data0_2) message.getData();
                    userManager.addUser(data.getUserId());
                    break;
                }
                case 1: {//入金
                    var data = (Data0_2) message.getData();
                    userManager.addMoney(data.getUserId(), data.getNum(), true);
                    break;
                }
                case 2: {//出金
                    var data = (Data0_2) message.getData();
                    userManager.subMoney(data.getUserId(), data.getNum());
                    break;
                }
                case 3, 5: {//开多 开空
                    var data = (Data3_7) message.getData();
                    Order order = new Order();
                    order.setOrderId(orderId++);
                    order.setUserId(data.getUserId());
                    order.setGoodsId(data.getGoodsId());
                    order.setType(data.getType());
                    order.setMargin(data.getMargin());
                    order.setFee(data.getFee());
                    order.setPrice(data.getPrice());
                    order.setNum(data.getNum());
                    order.setAvailableNum(data.getNum());
                    order.setCreateTime(data.getCreateTime());
                    deals = orderManager.openLongShort(order);
                    break;
                }
                case 4, 6: {//平多 平空
                    var data = (Data3_7) message.getData();
                    Order order = new Order();
                    order.setOrderId(orderId++);
                    order.setUserId(data.getUserId());
                    order.setGoodsId(data.getGoodsId());
                    order.setType(data.getType());
                    order.setMargin(data.getMargin());
                    order.setFee(data.getFee());
                    order.setPrice(data.getPrice());
                    order.setNum(data.getNum());
                    order.setAvailableNum(data.getNum());
                    order.setCreateTime(data.getCreateTime());
                    deals = orderManager.closeLongShort(order);
                    break;
                }
                case 7: {
                    var data = (Data3_7) message.getData();
                    Order order = new Order();
                    order.setOrderId(data.getOrderId());
                    order.setUserId(data.getUserId());
                    order.setGoodsId(data.getGoodsId());
                    order.setNum(data.getNum());
                    orderManager.cancelOrder(order);
                    break;
                }
                default: {
                    break;
                }
            }
            if (CollectionUtil.isNotEmpty(deals)) {
                for (Deal deal : deals) {
                    dealQueueHandler.publish(deal);
                }
            }
            resultQueueHandler.publish(Results.success(message));
        } catch (TradingEngineException e) {
            log.error("业务异常,消息={}", message, e);
            resultQueueHandler.publish(Results.error(e.getCode(), e.getMessage(), message));
        }
    }

}
