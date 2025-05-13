package com.dx1ngy.trading.common.bean;

import lombok.Data;

@Data
public class Data99 {
    private Long dealId;
    private Long goodsId;
    private Long buyUserId;
    private Long sellUserId;
    private Tick tick1;
    private Tick tick60;
    private Tick tick3600;
    private Tick tick86400;
}
