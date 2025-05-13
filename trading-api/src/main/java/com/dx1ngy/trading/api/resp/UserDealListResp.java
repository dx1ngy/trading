package com.dx1ngy.trading.api.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDealListResp {

    private List<Deal> dealList;

    @Data
    public static class Deal {
        private Long dealId;
        private BigDecimal price;
        private BigDecimal num;
        private BigDecimal total;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime dealTime;
    }
}
