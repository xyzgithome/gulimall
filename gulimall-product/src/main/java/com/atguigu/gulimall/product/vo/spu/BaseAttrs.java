/**
  * Copyright 2023 json.cn 
  */
package com.atguigu.gulimall.product.vo.spu;

import lombok.Data;

@Data
public class BaseAttrs {
    // 规格属性id
    private Long attrId;
    // 规格属性值
    private String attrValues;
    // 是否快速展示 0-false, 1-true
    private Integer showDesc;
}