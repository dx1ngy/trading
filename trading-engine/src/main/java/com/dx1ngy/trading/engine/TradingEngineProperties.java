package com.dx1ngy.trading.engine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "trading-engine")
public class TradingEngineProperties {
    private String initOffset;
    private String snapshotPath;
}
