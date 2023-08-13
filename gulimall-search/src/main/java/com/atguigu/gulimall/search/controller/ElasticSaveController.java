package com.atguigu.gulimall.search.controller;

import com.atguigu.common.dto.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.service.ElasticSaveService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {
    @Autowired
    private ElasticSaveService saveService;

    @PostMapping("/product")
    public R upProduct (@RequestBody List<SkuEsModel> modelList) {
        List<Long> failSkuIdList = saveService.upProduct(modelList);
        if (CollectionUtils.isEmpty(failSkuIdList)) {
            return R.ok();
        }

        return R.error().put("data", failSkuIdList);
    }

}
