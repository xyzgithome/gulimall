package com.atguigu.gulimall.product.feign.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.dto.SkuHasStockDTO;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.feign.client.WareFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WareFeignService {
    @Autowired
    private WareFeignClient client;

    public Map<Long, Boolean> getSkuStock(List<Long> skuIdList) {
        try {
            R r = client.getSkusHasStock(skuIdList);
            List<SkuHasStockDTO> resultList = JSONObject
                    .parseArray(JSONObject.toJSONString(r.get("data")), SkuHasStockDTO.class);

            return resultList.stream().collect(Collectors.toMap(SkuHasStockDTO::getSkuId, SkuHasStockDTO::getHasStock));
        } catch (Exception e) {
            log.error("调用库存服务失败, msg={}", e.getMessage());
            return null;
        }
    }
}
