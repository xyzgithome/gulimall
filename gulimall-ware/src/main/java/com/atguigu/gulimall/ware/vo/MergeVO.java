package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVO {
    // 采购单id
    private Long purchaseId;
    // 采购需求id集合
    private List<Long> items;
}
