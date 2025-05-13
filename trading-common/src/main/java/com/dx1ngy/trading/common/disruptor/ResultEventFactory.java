package com.dx1ngy.trading.common.disruptor;

import com.lmax.disruptor.EventFactory;

public class ResultEventFactory implements EventFactory<ResultEvent> {
    @Override
    public ResultEvent newInstance() {
        return new ResultEvent();
    }
}
