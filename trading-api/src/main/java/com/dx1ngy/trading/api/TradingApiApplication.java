package com.dx1ngy.trading.api;

import com.dx1ngy.trading.api.utils.StpUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = {"com.dx1ngy"})
@ConfigurationPropertiesScan
public class TradingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradingApiApplication.class, args);
        //初始化satoken
        StpUtil.USER.getLoginType();
    }

}
