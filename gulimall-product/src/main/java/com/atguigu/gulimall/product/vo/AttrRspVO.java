package com.atguigu.gulimall.product.vo;

import lombok.Data;

@Data
public class AttrRspVO extends AttrVO {
    private String catelogName;
    private String groupName;
    private Long[] catelogPath;
}
