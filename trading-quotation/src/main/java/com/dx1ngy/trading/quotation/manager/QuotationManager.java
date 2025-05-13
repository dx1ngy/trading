package com.dx1ngy.trading.quotation.manager;

import com.dx1ngy.trading.common.bean.Deal;
import com.dx1ngy.trading.common.bean.Tick;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
@Component
public class QuotationManager {

    //goodsId:dealId:Deal
    @Getter
    private final ConcurrentHashMap<Long, ConcurrentSkipListMap<Long, Deal>> goodsDealMap = new ConcurrentHashMap<>();

    //goodsId:interval:score:tick
    @Getter
    private final ConcurrentHashMap<Long, ConcurrentHashMap<Long, ConcurrentSkipListMap<Long, Tick>>> tickMap = new ConcurrentHashMap<>();

    public void addDeal(Deal deal) {
        Long goodsId = deal.getBuyOrder().getGoodsId();
        if (goodsDealMap.containsKey(goodsId)) {
            var dealMap = goodsDealMap.get(goodsId);
            addDeal(dealMap, deal);
        } else {
            var dealMap = new ConcurrentSkipListMap<Long, Deal>(Comparator.naturalOrder());
            addDeal(dealMap, deal);
            goodsDealMap.put(goodsId, dealMap);
        }
    }

    public Tick addTick(Deal deal, Long interval) {
        Long goodsId = deal.getBuyOrder().getGoodsId();
        Long score = getScore(deal.getDealTime(), interval);
        Tick tick;
        if (tickMap.containsKey(goodsId)) {
            var intervalScoreTickMap = tickMap.get(goodsId);
            if (intervalScoreTickMap.containsKey(interval)) {
                var scoreTickMap = intervalScoreTickMap.get(interval);
                if (scoreTickMap.containsKey(score)) {
                    tick = scoreTickMap.get(score);
                    tick.setClose(deal.getDealPrice());
                    if (deal.getDealPrice().compareTo(tick.getLow()) < 0) {
                        tick.setLow(deal.getDealPrice());
                    }
                    if (deal.getDealPrice().compareTo(tick.getHigh()) > 0) {
                        tick.setHigh(deal.getDealPrice());
                    }
                    tick.setVolume(tick.getVolume().add(deal.getDealNum()));
                    tick.setTurnover(tick.getTurnover().add(deal.getDealPrice().multiply(deal.getDealNum())));
                } else {
                    tick = addTick(scoreTickMap, deal, score);
                }
            } else {
                var scoreTickMap = new ConcurrentSkipListMap<Long, Tick>(Comparator.naturalOrder());
                tick = addTick(scoreTickMap, deal, score);
                intervalScoreTickMap.put(interval, scoreTickMap);
            }
        } else {
            var intervalScoreTickMap = new ConcurrentHashMap<Long, ConcurrentSkipListMap<Long, Tick>>();
            var scoreTickMap = new ConcurrentSkipListMap<Long, Tick>(Comparator.naturalOrder());
            tick = addTick(scoreTickMap, deal, score);
            intervalScoreTickMap.put(interval, scoreTickMap);
            tickMap.put(goodsId, intervalScoreTickMap);
        }
        return tick;
    }

    private void addDeal(ConcurrentSkipListMap<Long, Deal> dealMap, Deal deal) {
        dealMap.put(deal.getDealId(), deal);
        if (dealMap.size() > 10) {
            dealMap.pollFirstEntry();
        }
    }

    private long getScore(LocalDateTime dateTime, long intervalSeconds) {
        long totalSeconds = dateTime.toLocalTime().toSecondOfDay();
        long truncatedSeconds = (totalSeconds / intervalSeconds) * intervalSeconds;
        return dateTime.with(LocalTime.ofSecondOfDay(truncatedSeconds))
                .atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
    }

    private Tick addTick(ConcurrentSkipListMap<Long, Tick> scoreTickMap, Deal deal, Long score) {
        var tick = new Tick();
        tick.setTimestamp(score);
        tick.setOpen(deal.getDealPrice());
        tick.setClose(deal.getDealPrice());
        tick.setHigh(deal.getDealPrice());
        tick.setLow(deal.getDealPrice());
        tick.setVolume(deal.getDealNum());
        tick.setTurnover(deal.getDealPrice().multiply(deal.getDealNum()));
        scoreTickMap.put(score, tick);
        return tick;
    }
}
