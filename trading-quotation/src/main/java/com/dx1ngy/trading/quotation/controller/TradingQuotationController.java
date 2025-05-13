package com.dx1ngy.trading.quotation.controller;

import com.dx1ngy.trading.common.bean.Deal;
import com.dx1ngy.trading.common.bean.Tick;
import com.dx1ngy.trading.quotation.manager.QuotationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class TradingQuotationController {

    private final QuotationManager quotationManager;

    public TradingQuotationController(QuotationManager quotationManager) {
        this.quotationManager = quotationManager;
    }

    @PostMapping("/user-deal-list")
    public List<Deal> userDealList(@RequestBody Map<String, Long> req) {
        Long goodsId = req.get("goodsId");
        var goodsDealMap = quotationManager.getGoodsDealMap();
        List<Deal> list = new ArrayList<>();
        if (goodsDealMap.containsKey(goodsId)) {
            list = goodsDealMap.get(goodsId).values().stream().toList();
        }
        return list;
    }

    @PostMapping("/user-tick-list")
    public List<Tick> userTickList(@RequestBody Map<String, Long> req) {
        Long goodsId = req.get("goodsId");
        Long interval = req.get("interval");
        List<Tick> list = new ArrayList<>();
        var tickMap = quotationManager.getTickMap();
        if (tickMap.containsKey(goodsId)) {
            var intervalScoreTickMap = tickMap.get(goodsId);
            if (intervalScoreTickMap.containsKey(interval)) {
                list = intervalScoreTickMap.get(interval).values().stream().toList();
            }
        }
        return list;
    }

}
