/**
 * Copyright 2023 json.cn
 */
package com.atguigu.gulimall.product.vo.spu;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SpuSaveVO {
    // 商品名称
    private String spuName;
    // 商品描述
    private String spuDescription;
    // 选择分类ID
    private Long catalogId;
    // 选择品牌ID
    private Long brandId;
    // 商品重量(Kg)
    private BigDecimal weight;
    // 发布状态
    private Integer publishStatus;
    // 商品介绍-图片
    private List<String> decript;
    // 商品图集-图片
    private List<String> images;
    // 设置积分
    private Bounds bounds;
    // 规格参数
    private List<BaseAttrs> baseAttrs;
    private List<Skus> skus;
}