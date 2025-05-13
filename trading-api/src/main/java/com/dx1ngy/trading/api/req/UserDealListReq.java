package com.dx1ngy.trading.api.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDealListReq {
    @NotNull
    @Min(1)
    private Long goodsId;
}
