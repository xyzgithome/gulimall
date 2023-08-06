/**
 * Copyright 2023 json.cn
 */
package com.atguigu.gulimall.product.vo.spu;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Bounds {
    // 金币积分
    private BigDecimal buyBounds;
    // 成长值积分
    private BigDecimal growBounds;
}