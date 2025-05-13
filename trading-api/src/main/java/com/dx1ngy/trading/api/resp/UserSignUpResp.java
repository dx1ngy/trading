package com.dx1ngy.trading.api.resp;

import lombok.Data;

@Data
public class UserSignUpResp {
    private Long userId;
    private String username;
    private String token;
}
