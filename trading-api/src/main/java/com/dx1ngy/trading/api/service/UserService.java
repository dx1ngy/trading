package com.dx1ngy.trading.api.service;

import com.dx1ngy.trading.api.entity.UserEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dx1ngy.trading.api.req.*;
import com.dx1ngy.trading.api.resp.*;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author dx1ngy
 * @since 2025-03-11
 */
public interface UserService extends IService<UserEntity> {

    UserSignUpResp signUp(UserSignUpReq req);

    void moneyIn(UserMoneyInReq req);

    void moneyOut(UserMoneyOutReq req);

    void openLong(UserOpenLongReq req);

    void closeLong(UserCloseLongReq req);

    void openShort(UserOpenShortReq req);

    void closeShort(UserCloseShortReq req);

    void cancel(UserCancelReq req);

    UserInfoResp info(UserInfoReq req);

    UserGoodsInfoResp goodsInfo(UserGoodsInfoReq req);

    UserOrderListResp orderList(UserOrderListReq req);

    UserOrderBookResp orderBook(UserOrderBookReq req);

    UserDealListResp dealList(UserDealListReq req);

    UserKlineResp kline(UserKlineReq req);

}
