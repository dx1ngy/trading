package com.dx1ngy.core;

import lombok.Data;

@Data
public class Result {
    private Integer code;
    private String message;
    private Object data;
}
