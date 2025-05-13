package com.dx1ngy.trading.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
/**
 * <p>
 * 
 * </p>
 *
 * @author dx1ngy
 * @since 2025-05-12
 */
@Getter
@Setter
@ToString
@TableName("message")
@Accessors(chain = true)
public class MessageEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("topic")
    private String topic;

    /**
     * 0注册1充钱2提现3开多4平多5开空6平空7取消
     */
    @TableField("`type`")
    private Integer type;

    @TableField("`data`")
    private String data;

    @TableField("create_time")
    private LocalDateTime createTime;

    public static final String ID = "id";

    public static final String TOPIC = "topic";

    public static final String TYPE = "type";

    public static final String DATA = "data";

    public static final String CREATE_TIME = "create_time";
}
