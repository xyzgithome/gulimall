/**
 * Copyright 2023 json.cn
 */
package com.atguigu.gulimall.product.vo.spu;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberPrice {
    // 会员id
    private Long id;
    // 会员名称
    private String name;
    // 会员价格
    private BigDecimal price;
}