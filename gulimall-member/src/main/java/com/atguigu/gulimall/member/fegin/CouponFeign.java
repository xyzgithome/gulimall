package com.atguigu.gulimall.member.fegin;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 优惠券信息
 *
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-02 10:19:22
 */
@FeignClient("gulimall-coupon")
public interface CouponFeign {

    @RequestMapping(value = "coupon/coupon/list", method = RequestMethod.GET)
    public R list(@RequestParam Map<String, Object> params);
}
