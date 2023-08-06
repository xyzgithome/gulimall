/**
 * Copyright 2023 json.cn
 */
package com.atguigu.gulimall.product.vo.spu;

import lombok.Data;

@Data
public class Attr {
    // 销售属性id
    private Long attrId;
    // 销售属性名
    private String attrName;
    // 销售属性值
    private String attrValue;
}