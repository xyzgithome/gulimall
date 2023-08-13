package com.atguigu.common.dto;

import lombok.Data;

@Data
public class SkuHasStockDTO {
    private Long skuId;
    private Boolean hasStock;
}
