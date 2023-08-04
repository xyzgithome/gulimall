/**
 * Copyright 2023 json.cn
 */
package com.atguigu.gulimall.product.vo.spu;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPrice {
    private Long id;
    private String name;
    private BigDecimal price;
}