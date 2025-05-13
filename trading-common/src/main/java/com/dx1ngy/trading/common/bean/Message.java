package com.dx1ngy.trading.common.bean;


import lombok.Data;

@Data
public class Message {
    private Long messageId;
    private Integer messageType;
    private Object data;
}
