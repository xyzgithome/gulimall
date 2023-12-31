package com.atguigu.gulimall.ware.service;

import com.atguigu.common.dto.SkuHasStockDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-02 11:04:29
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockDTO> getSkusHasStock(List<Long> skuIds);
}

