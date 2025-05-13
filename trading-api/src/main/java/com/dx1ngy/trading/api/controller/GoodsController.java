package com.dx1ngy.trading.api.controller;

import com.dx1ngy.core.advice.WrapResponse;
import com.dx1ngy.trading.api.entity.GoodsEntity;
import com.dx1ngy.trading.api.service.GoodsService;
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
 *  前端控制器
 * </p>
 *
 * @author dx1ngy
 * @since 2025-03-18
 */
@Tag(name = "goods-entity")
@RestController
@RequestMapping("/goods-entity")
@WrapResponse
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @Operation(summary = "goods-entity", security = @SecurityRequirement(name = "token"))
    @PostMapping("/demo")
    public void demo(@Validated @RequestBody GoodsEntity goodsEntity) {
    }

}
