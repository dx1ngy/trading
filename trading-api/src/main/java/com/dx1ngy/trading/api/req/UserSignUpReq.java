package com.dx1ngy.trading.api.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserSignUpReq {
    @NotBlank
    private String username;
}
