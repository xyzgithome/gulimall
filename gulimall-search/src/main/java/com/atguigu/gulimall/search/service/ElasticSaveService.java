package com.atguigu.gulimall.search.service;

import com.atguigu.common.dto.es.SkuEsModel;

import java.util.List;

public interface ElasticSaveService {

    List<Long> upProduct(List<SkuEsModel> modelList);
}
