package com.dx1ngy.trading.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
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
@TableName("goods")
@Accessors(chain = true)
public class GoodsEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("`name`")
    private String name;

    /**
     * 保证金
     */
    @TableField("margin")
    private BigDecimal margin;

    /**
     * 手续费
     */
    @TableField("fee")
    private BigDecimal fee;

    /**
     * 初始价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 涨停比例
     */
    @TableField("up_rate")
    private BigDecimal upRate;

    /**
     * 跌停比例
     */
    @TableField("down_rate")
    private BigDecimal downRate;

    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String MARGIN = "margin";

    public static final String FEE = "fee";

    public static final String PRICE = "price";

    public static final String UP_RATE = "up_rate";

    public static final String DOWN_RATE = "down_rate";
}
