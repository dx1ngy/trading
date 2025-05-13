package com.dx1ngy.trading.common.bean;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Goods {
    private Long goodsId;
    private Long userId;
    //0多仓 2空仓
    private Integer type;
    private BigDecimal dealPrice;
    private BigDecimal dealNum;
    private BigDecimal margin;
}
