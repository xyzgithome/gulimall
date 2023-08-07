package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseDoneVO {
    // 采购单id
    private Long id;

    // 完成/失败的采购需求详情
    private List<PurchaseDetailVO> items;
}
