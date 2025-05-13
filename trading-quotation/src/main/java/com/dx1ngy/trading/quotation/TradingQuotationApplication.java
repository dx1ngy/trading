package com.dx1ngy.trading.quotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.dx1ngy"})
@ConfigurationPropertiesScan
public class TradingQuotationApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingQuotationApplication.class, args);
    }

}
