package com.dx1ngy.trading.api.resp;

import com.dx1ngy.trading.common.bean.Tick;
import lombok.Data;

import java.util.List;

@Data
public class UserKlineResp {
    private List<Tick> tickList;
}
