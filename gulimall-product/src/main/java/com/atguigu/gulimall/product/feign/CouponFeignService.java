package com.atguigu.gulimall.product.feign;

import com.atguigu.common.dto.SkuReductionDTO;
import com.atguigu.common.dto.SpuBoundDTO;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-coupon")
public interface CouponFeignService {
    @PostMapping("coupon/spubounds/save")
    R save(@RequestBody SpuBoundDTO spuBoundDTO);

    @PostMapping("coupon/skufullreduction/save/info")
    R saveInfo(@RequestBody SkuReductionDTO skuReductionDTO);
}
