package com.atguigu.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpuBoundDTO {
    private BigDecimal buyBounds;
    private Long spuId;
    private BigDecimal growBounds;
    private String name;
}
