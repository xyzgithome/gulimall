/**
 * Copyright 2023 json.cn
 */
package com.atguigu.gulimall.product.vo.spu;

import lombok.Data;

@Data
public class Images {
    // 图片oss地址
    private String imgUrl;
    // 是否是默认图片 0-false, 1-true
    private Integer defaultImg;
}