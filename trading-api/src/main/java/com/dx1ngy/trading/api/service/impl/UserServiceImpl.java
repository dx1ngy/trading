package com.dx1ngy.trading.api.service.impl;

import cn.hutool.http.HttpRequest;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dx1ngy.core.utils.JsonUtil;
import com.dx1ngy.core.utils.SpringUtil;
import com.dx1ngy.trading.api.TradingApiCode;
import com.dx1ngy.trading.api.TradingApiException;
import com.dx1ngy.trading.api.TradingApiProperties;
import com.dx1ngy.trading.api.entity.GoodsEntity;
import com.dx1ngy.trading.api.entity.MessageEntity;
import com.dx1ngy.trading.api.entity.UserEntity;
import com.dx1ngy.trading.api.manager.RequestEvent;
import com.dx1ngy.trading.api.mapper.UserMapper;
import com.dx1ngy.trading.api.req.*;
import com.dx1ngy.trading.api.resp.*;
import com.dx1ngy.trading.api.service.GoodsService;
import com.dx1ngy.trading.api.service.MessageService;
import com.dx1ngy.trading.api.service.UserService;
import com.dx1ngy.trading.api.utils.StpUtil;
import com.dx1ngy.trading.common.bean.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dx1ngy
 * @since 2025-03-11
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private final MessageService messageService;
    private final GoodsService goodsService;
    private final TradingApiProperties tradingApiProperties;

    public UserServiceImpl(MessageService messageService,
                           GoodsService goodsService,
                           TradingApiProperties tradingApiProperties) {
        this.messageService = messageService;
        this.goodsService = goodsService;
        this.tradingApiProperties = tradingApiProperties;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserSignUpResp signUp(UserSignUpReq req) {
        var user = query().eq(UserEntity.USERNAME, req.getUsername()).one();
        if (user == null) {
            user = new UserEntity();
            user.setUsername(req.getUsername());
            save(user);
            var data = new Data0_2();
            data.setUserId(user.getId());
            sendMessage(0, data);
        }
        StpUtil.USER.login(user.getId());
        var resp = new UserSignUpResp();
        resp.setUserId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setToken(StpUtil.USER.getTokenValue());
        return resp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moneyIn(UserMoneyInReq req) {
        var data = new Data0_2();
        data.setUserId(Long.parseLong((String) StpUtil.USER.getLoginId()));
        data.setNum(req.getNum());
        sendMessage(1, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moneyOut(UserMoneyOutReq req) {
        var data = new Data0_2();
        data.setUserId(Long.parseLong((String) StpUtil.USER.getLoginId()));
        data.setNum(req.getNum());
        sendMessage(2, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void openLong(UserOpenLongReq req) {
        var goods = goodsService.getById(req.getGoodsId());
        checkPrice(goods, req.getPrice());
        var data = new Data3_7();
        data.setUserId(Long.parseLong((String) StpUtil.USER.getLoginId()));
        data.setGoodsId(goods.getId());
        data.setType(0);
        data.setMargin(goods.getMargin());
        data.setFee(goods.getFee());
        data.setPrice(req.getPrice());
        data.setNum(req.getNum());
        data.setAvailableNum(req.getNum());
        data.setCreateTime(LocalDateTime.now());
        sendMessage(3, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeLong(UserCloseLongReq req) {
        var goods = goodsService.getById(req.getGoodsId());
        checkPrice(goods, req.getPrice());
        var data = new Data3_7();
        data.setUserId(Long.parseLong((String) StpUtil.USER.getLoginId()));
        data.setGoodsId(goods.getId());
        data.setType(1);
        data.setMargin(goods.getMargin());
        data.setFee(goods.getFee());
        data.setPrice(req.getPrice());
        data.setNum(req.getNum());
        data.setAvailableNum(req.getNum());
        data.setCreateTime(LocalDateTime.now());
        sendMessage(4, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void openShort(UserOpenShortReq req) {
        var goods = goodsService.getById(req.getGoodsId());
        checkPrice(goods, req.getPrice());
        var data = new Data3_7();
        data.setUserId(Long.parseLong((String) StpUtil.USER.getLoginId()));
        data.setGoodsId(goods.getId());
        data.setType(2);
        data.setMargin(goods.getMargin());
        data.setFee(goods.getFee());
        data.setPrice(req.getPrice());
        data.setNum(req.getNum());
        data.setAvailableNum(req.getNum());
        data.setCreateTime(LocalDateTime.now());
        sendMessage(5, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeShort(UserCloseShortReq req) {
        var goods = goodsService.getById(req.getGoodsId());
        checkPrice(goods, req.getPrice());
        var data = new Data3_7();
        data.setUserId(Long.parseLong((String) StpUtil.USER.getLoginId()));
        data.setGoodsId(goods.getId());
        data.setType(3);
        data.setMargin(goods.getMargin());
        data.setFee(goods.getFee());
        data.setPrice(req.getPrice());
        data.setNum(req.getNum());
        data.setAvailableNum(req.getNum());
        data.setCreateTime(LocalDateTime.now());
        sendMessage(6, data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(UserCancelReq req) {
        var data = new Data3_7();
        data.setUserId(Long.parseLong((String) StpUtil.USER.getLoginId()));
        data.setOrderId(req.getOrderId());
        data.setGoodsId(req.getGoodsId());
        data.setNum(req.getNum());
        sendMessage(7, data);
    }

    @Override
    public UserInfoResp info(UserInfoReq req) {
        var map = new HashMap<String, Object>();
        map.put("userId", StpUtil.USER.getLoginId());
        map.put("goodsId", req.getGoodsId());
        try (var response = HttpRequest.post(tradingApiProperties.getEngineBaseUrl() + "/user-info")
                .header("Content-Type", "application/json")
                .body(JsonUtil.toJson(map))
                .execute()) {
            return JsonUtil.fromJson(response.body(), UserInfoResp.class);
        }
    }

    @Override
    public UserGoodsInfoResp goodsInfo(UserGoodsInfoReq req) {
        var goods = goodsService.getById(req.getGoodsId());
        var resp = new UserGoodsInfoResp();
        var map = new HashMap<String, Object>();
        map.put("goodsId", req.getGoodsId());
        try (var response = HttpRequest.post(tradingApiProperties.getEngineBaseUrl() + "/goods-price")
                .header("Content-Type", "application/json")
                .body(JsonUtil.toJson(map))
                .execute()) {
            var res = JsonUtil.fromJson(response.body(), new TypeReference<Map<String, Object>>() {
            });
            resp.setCurrentPrice(res.get("price") == null ? goods.getPrice() : new BigDecimal(String.valueOf(res.get("price"))));
            resp.setUpPrice(getUpPrice(goods));
            resp.setDownPrice(getDownPrice(goods));
            resp.setMargin(goods.getMargin());
            resp.setFee(goods.getFee());
            return resp;
        }
    }

    @Override
    public UserOrderListResp orderList(UserOrderListReq req) {
        var map = new HashMap<String, Object>();
        map.put("userId", StpUtil.USER.getLoginId());
        map.put("goodsId", req.getGoodsId());
        try (var response = HttpRequest.post(tradingApiProperties.getEngineBaseUrl() + "/user-order-list")
                .header("Content-Type", "application/json")
                .body(JsonUtil.toJson(map))
                .execute()) {
            var orderList = JsonUtil.fromJson(response.body(), new TypeReference<List<Order>>() {
            });
            var resp = new UserOrderListResp();
            resp.setOrderList(orderList);
            return resp;
        }
    }

    @Override
    public UserOrderBookResp orderBook(UserOrderBookReq req) {
        var map = new HashMap<String, Object>();
        map.put("goodsId", req.getGoodsId());
        try (var response = HttpRequest.post(tradingApiProperties.getEngineBaseUrl() + "/user-order-book")
                .header("Content-Type", "application/json")
                .body(JsonUtil.toJson(map))
                .execute()) {
            return JsonUtil.fromJson(response.body(), UserOrderBookResp.class);
        }
    }

    @Override
    public UserDealListResp dealList(UserDealListReq req) {
        var dealList = new ArrayList<UserDealListResp.Deal>();
        var map = new HashMap<String, Object>();
        map.put("goodsId", req.getGoodsId());
        try (var response = HttpRequest.post(tradingApiProperties.getQuotationBaseUrl() + "/user-deal-list")
                .header("Content-Type", "application/json")
                .body(JsonUtil.toJson(map))
                .execute()) {
            var list = JsonUtil.fromJson(response.body(), new TypeReference<List<Deal>>() {
            });
            for (Deal d : list) {
                var deal = new UserDealListResp.Deal();
                deal.setDealId(d.getDealId());
                deal.setPrice(d.getDealPrice());
                deal.setNum(d.getDealNum());
                deal.setTotal(d.getDealPrice().multiply(d.getDealNum()));
                deal.setDealTime(d.getDealTime());
                dealList.add(deal);
            }
        }
        var resp = new UserDealListResp();
        resp.setDealList(dealList);
        return resp;
    }

    @Override
    public UserKlineResp kline(UserKlineReq req) {
        var map = new HashMap<String, Object>();
        map.put("goodsId", req.getGoodsId());
        map.put("interval", req.getInterval());
        try (var response = HttpRequest.post(tradingApiProperties.getQuotationBaseUrl() + "/user-tick-list")
                .header("Content-Type", "application/json")
                .body(JsonUtil.toJson(map))
                .execute()) {
            var tickList = JsonUtil.fromJson(response.body(), new TypeReference<List<Tick>>() {
            });
            var resp = new UserKlineResp();
            resp.setTickList(tickList);
            return resp;
        }
    }

    private void sendMessage(Integer type, Object data) {
        var messageEntity = new MessageEntity();
        messageEntity.setTopic("request");
        messageEntity.setType(type);
        messageEntity.setData(JsonUtil.toJson(data));
        messageEntity.setCreateTime(LocalDateTime.now());
        messageService.save(messageEntity);
        var message = new Message();
        message.setMessageId(messageEntity.getId());
        message.setMessageType(messageEntity.getType());
        message.setData(data);
        SpringUtil.getApplicationContext().publishEvent(new RequestEvent(message));
    }

    private void checkPrice(GoodsEntity goods, BigDecimal price) {
        if (price.compareTo(getUpPrice(goods)) > 0 || price.compareTo(getDownPrice(goods)) < 0) {
            throw new TradingApiException(TradingApiCode.ERR_4);
        }
    }

    private BigDecimal getUpPrice(GoodsEntity goods) {
        return goods.getPrice().multiply(BigDecimal.ONE.add(goods.getUpRate()));
    }

    private BigDecimal getDownPrice(GoodsEntity goods) {
        return goods.getPrice().multiply(BigDecimal.ONE.subtract(goods.getDownRate()));
    }
}
