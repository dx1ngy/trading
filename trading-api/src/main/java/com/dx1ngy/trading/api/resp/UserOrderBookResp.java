package com.dx1ngy.trading.api.resp;

import com.dx1ngy.trading.common.bean.OrderBook;
import lombok.Data;

import java.util.List;

@Data
public class UserOrderBookResp {
    private List<OrderBook> buyList;
    private List<OrderBook> sellList;
}
