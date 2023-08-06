/**
 * Copyright 2023 json.cn
 */
package com.atguigu.gulimall.product.vo.spu;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Skus {
    // 销售属性
    private List<Attr> attr;
    // 商品名称
    private String skuName;
    // 商品价格
    private BigDecimal price;
    // 商品标题
    private String skuTitle;
    // 商品副标题
    private String skuSubtitle;
    // sku信息-选择的图集
    private List<Images> images;
    // 颜色+版本信息 ex: ["玫瑰金","12GB+256GB"]
    private List<String> descar;
    // 设置折扣-满多少件打多少折扣
    private Integer fullCount;
    // 设置折扣-满多少件打多少折扣
    private BigDecimal discount;
    // 是否可叠加折扣优惠 0-false, 1-true
    private Integer countStatus;
    // 设置满减-满多少减去多少元
    private BigDecimal fullPrice;
    // 设置满减-满多少减去多少元
    private BigDecimal reducePrice;
    // 是否可叠加满减优惠 0-false, 1-true
    private Integer priceStatus;
    // 设置会员价格
    private List<MemberPrice> memberPrice;
}