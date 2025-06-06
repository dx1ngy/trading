package com.dx1ngy.trading.api.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserCloseLongReq {
    @NotNull
    @Min(1)
    private Long goodsId;
    @NotNull
    @Min(1)
    private BigDecimal price;
    @NotNull
    @Min(1)
    private BigDecimal num;
}
