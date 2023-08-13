package com.atguigu.gulimall.product.feign.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.dto.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.feign.client.ESFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Objects;

@Service
public class ESFeignService {
    @Autowired
    private ESFeignClient client;

    /**
     * Feign调用流程
     * 1、构造请求数据，将对象转为json
     *      RequestTemplate template = buildTemplateFromArgs.create(argv);
     * 2、发送请求进行执行（执行成功会解码响应数据）
     *      executeAndDecode(template)
     * 3、执行请求会有重试机制
     *      while(true){
     *          try{
     *              executeAndDecode(template);
     *          }catch(){
     *              retryer.continueOrPropagate(e);
     *              throw ex;
     *              continue;
     *          }
     *      }
     */
    public Boolean upProduct (@RequestBody List<SkuEsModel> modelList) {
        R r = client.upProduct(modelList);

        // 修改sku状态
        if (Objects.equals(0, r.getCode())) {
            return true;
        }

        // TODO 获取上架失败的skuId，重试
        List<Long> failUPSkuIdList = JSONObject.parseArray(JSONObject.toJSONString(r.get("data")), Long.class);

        return false;
    }
}
