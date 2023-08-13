package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.dto.es.SkuEsModel;
import com.atguigu.common.exception.BizCodeEnum;
import com.atguigu.common.exception.GLException;
import com.atguigu.gulimall.search.config.ElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.service.ElasticSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ElasticSaveServiceImpl implements ElasticSaveService {
    @Autowired
    private RestHighLevelClient client;

    @Override
    public List<Long> upProduct(List<SkuEsModel> modelList) {
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel model : modelList) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(String.valueOf(model.getSkuId()));
            indexRequest.source(JSONObject.toJSONString(model), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }

        BulkResponse res;
        try {
            res = client.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        } catch (Exception e) {
            log.error("商品上架异常, msg={}", e.getMessage());
            throw new GLException(BizCodeEnum.PRODUCT_UP_ERROR);
        }

        // ES保存商品成功
        if (!res.hasFailures()) {
            return Collections.emptyList();
        }

        // 保存失败
        return Arrays.stream(res.getItems()).filter(BulkItemResponse::isFailed)
                .map(item -> Long.valueOf(item.getId())).collect(Collectors.toList());
    }
}
