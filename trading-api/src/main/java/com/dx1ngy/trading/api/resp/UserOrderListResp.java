package com.dx1ngy.trading.api.resp;

import com.dx1ngy.trading.common.bean.Order;
import lombok.Data;

import java.util.List;

@Data
public class UserOrderListResp {
    private List<Order> orderList;
}
