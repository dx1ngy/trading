package com.dx1ngy.trading.api;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "trading-api")
public class TradingApiProperties {
    private String engineBaseUrl;
    private String quotationBaseUrl;
}
