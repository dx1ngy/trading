package com.dx1ngy.trading.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.dx1ngy.core.advice.WrapResponse;
import com.dx1ngy.trading.api.req.*;
import com.dx1ngy.trading.api.resp.*;
import com.dx1ngy.trading.api.service.UserService;
import com.dx1ngy.trading.api.utils.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author dx1ngy
 * @since 2025-03-11
 */
@Tag(name = "用户")
@RestController
@RequestMapping("/user")
@WrapResponse
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "注册")
    @PostMapping("/sign-up")
    public UserSignUpResp signUp(@Validated @RequestBody UserSignUpReq req) {
        return userService.signUp(req);
    }

    @Operation(summary = "入金", security = @SecurityRequirement(name = "token"))
    @PostMapping("/money-in")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public void moneyIn(@Validated @RequestBody UserMoneyInReq req) {
        userService.moneyIn(req);
    }

    @Operation(summary = "出金", security = @SecurityRequirement(name = "token"))
    @PostMapping("/money-out")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public void moneyOut(@Validated @RequestBody UserMoneyOutReq req) {
        userService.moneyOut(req);
    }

    @Operation(summary = "开多", security = @SecurityRequirement(name = "token"))
    @PostMapping("/open-long")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public void openLong(@Validated @RequestBody UserOpenLongReq req) {
        userService.openLong(req);
    }

    @Operation(summary = "平多", security = @SecurityRequirement(name = "token"))
    @PostMapping("/close-long")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public void closeLong(@Validated @RequestBody UserCloseLongReq req) {
        userService.closeLong(req);
    }

    @Operation(summary = "开空", security = @SecurityRequirement(name = "token"))
    @PostMapping("/open-short")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public void openShort(@Validated @RequestBody UserOpenShortReq req) {
        userService.openShort(req);
    }

    @Operation(summary = "平空", security = @SecurityRequirement(name = "token"))
    @PostMapping("/close-short")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public void closeShort(@Validated @RequestBody UserCloseShortReq req) {
        userService.closeShort(req);
    }

    @Operation(summary = "取消", security = @SecurityRequirement(name = "token"))
    @PostMapping("/cancel")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public void cancel(@Validated @RequestBody UserCancelReq req) {
        userService.cancel(req);
    }

    @Operation(summary = "用户基本信息", security = @SecurityRequirement(name = "token"))
    @PostMapping("/info")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public UserInfoResp info(@Validated @RequestBody UserInfoReq req) {
        return userService.info(req);
    }

    @Operation(summary = "商品基本信息", security = @SecurityRequirement(name = "token"))
    @PostMapping("/goods-info")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public UserGoodsInfoResp goodsInfo(@Validated @RequestBody UserGoodsInfoReq req) {
        return userService.goodsInfo(req);
    }

    @Operation(summary = "订单列表", security = @SecurityRequirement(name = "token"))
    @PostMapping("/order-list")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public UserOrderListResp orderList(@Validated @RequestBody UserOrderListReq req) {
        return userService.orderList(req);
    }

    @Operation(summary = "订单队列", security = @SecurityRequirement(name = "token"))
    @PostMapping("/order-book")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public UserOrderBookResp orderBook(@Validated @RequestBody UserOrderBookReq req) {
        return userService.orderBook(req);
    }

    @Operation(summary = "最近成交列表", security = @SecurityRequirement(name = "token"))
    @PostMapping("/deal-list")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public UserDealListResp dealList(@Validated @RequestBody UserDealListReq req) {
        return userService.dealList(req);
    }

    @Operation(summary = "最近成交列表", security = @SecurityRequirement(name = "token"))
    @PostMapping("/kline")
    @SaCheckLogin(type = StpUtil.USER_TYPE)
    public UserKlineResp kline(@Validated @RequestBody UserKlineReq req) {
        return userService.kline(req);
    }


}
