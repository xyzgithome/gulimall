package com.atguigu.gulimall.coupon.service;

import com.atguigu.common.dto.SkuReductionDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-02 10:19:22
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * //sku的优惠、满减等信息
     * gulimall_sms -> sms_sku_ladder/sms_sku_full_reduction/sms_member_price
     *
     * @param skuReductionDTO dto
     */
    void saveInfo(SkuReductionDTO skuReductionDTO);
}

