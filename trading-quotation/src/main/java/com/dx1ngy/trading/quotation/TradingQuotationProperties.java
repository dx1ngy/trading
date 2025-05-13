package com.dx1ngy.trading.quotation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "trading-quotation")
public class TradingQuotationProperties {
    private String initOffset;
}
