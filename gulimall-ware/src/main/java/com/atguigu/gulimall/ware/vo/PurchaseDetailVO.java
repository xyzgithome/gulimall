package com.atguigu.gulimall.ware.vo;

import lombok.Data;

@Data
public class PurchaseDetailVO {
    // 采购需求id
    private Long itemId;
    // 采购需求状态
    private Integer status;
    // 原因
    private String reason;
}
