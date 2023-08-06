package com.atguigu.common.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundDTO {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
