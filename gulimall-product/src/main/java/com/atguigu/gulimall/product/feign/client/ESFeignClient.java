package com.atguigu.gulimall.product.feign.client;

import com.atguigu.common.dto.es.SkuEsModel;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-search")
public interface ESFeignClient {

    @PostMapping("/search/save/product")
    R upProduct (@RequestBody List<SkuEsModel> modelList);

}
