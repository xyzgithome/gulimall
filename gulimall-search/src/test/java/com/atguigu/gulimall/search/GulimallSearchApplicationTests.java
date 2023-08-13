package com.atguigu.gulimall.search;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.common.dto.SpuBoundDTO;
import com.atguigu.gulimall.search.config.ElasticSearchConfig;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.ServerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GulimallSearchApplicationTests {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void test() {
        System.out.println(restHighLevelClient);
    }

    @Test
    public void testDelete() throws Exception {
        DeleteRequest deleteRequest = new DeleteRequest("users", "0");
        DeleteResponse response = restHighLevelClient.delete(deleteRequest, ElasticSearchConfig.COMMON_OPTIONS);

        System.out.println(response.getResult());
    }

    /**
     * 批量同步新增、更新 数据
     */
    @Test
    public void testBatchIndexDataSync() throws IOException {
        SpuBoundDTO spuBound1 = SpuBoundDTO.builder().spuId(1L)
                .buyBounds(new BigDecimal("100"))
                .growBounds(new BigDecimal("100"))
                .name("手机").build();

        SpuBoundDTO spuBound2 = SpuBoundDTO.builder().spuId(2L)
                .buyBounds(new BigDecimal("200"))
                .growBounds(new BigDecimal("200")).name("手机").build();

        SpuBoundDTO spuBound3 = SpuBoundDTO.builder().spuId(3L)
                .buyBounds(new BigDecimal("300"))
                .growBounds(new BigDecimal("300")).name("手机").build();

        SpuBoundDTO spuBound4 = SpuBoundDTO.builder().spuId(4L)
                .buyBounds(new BigDecimal("400"))
                .growBounds(new BigDecimal("400")).name("电脑").build();

        SpuBoundDTO spuBound5 = SpuBoundDTO.builder().spuId(5L)
                .buyBounds(new BigDecimal("300"))
                .growBounds(new BigDecimal("500")).name("手机").build();


        List<SpuBoundDTO> spuBoundList = Arrays.asList(spuBound1, spuBound2, spuBound3, spuBound4, spuBound5);

        BulkRequest bulkRequest = new BulkRequest();

        for (int i = 0; i < spuBoundList.size(); i++) {
            IndexRequest indexRequest = new IndexRequest("users");
            indexRequest.id(String.valueOf(i+1));
            indexRequest.source(JSONObject.toJSONString(spuBoundList.get(i)), XContentType.JSON);

            bulkRequest.add(indexRequest);
        }

        BulkResponse response = restHighLevelClient.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(response.status());
    }

    /**
     * 同步新增、更新 数据
     */
    @Test
    public void testIndexDataSync() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        SpuBoundDTO spuBound = SpuBoundDTO.builder().spuId(1L)
                .buyBounds(new BigDecimal("100"))
                .growBounds(new BigDecimal("100")).build();

        String js = JSONObject.toJSONString(spuBound);
        indexRequest.source(js, XContentType.JSON);

        IndexResponse response = restHighLevelClient.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(response);
    }

    /**
     * 异步新增、更新 数据
     */
    @Test
    public void testIndexDataAsync() throws InterruptedException {
        IndexRequest indexRequest = new IndexRequest("spu");
        indexRequest.id("1");
        SpuBoundDTO spuBound = SpuBoundDTO.builder().spuId(2L)
                .buyBounds(new BigDecimal("200"))
                .growBounds(new BigDecimal("300")).build();

        String js = JSONObject.toJSONString(spuBound);
        indexRequest.source(js, XContentType.JSON);

        restHighLevelClient
                .indexAsync(indexRequest, ElasticSearchConfig.COMMON_OPTIONS, new ActionListener<IndexResponse>() {
                    @Override
                    public void onResponse(IndexResponse indexResponse) {
                        System.out.println(indexResponse.status());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
        // 线程睡2s，保证回调时程序没有结束
        Thread.sleep(2000);
    }

    /**
     * 复杂索引的检索
     */
    @Test
    public void testSearchNormal() throws IOException {
        SearchRequest searchRequest = new SearchRequest();

        searchRequest.indices("users");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("buyBounds", 100));
        sourceBuilder.from(0);
        sourceBuilder.size(2);

        searchRequest.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);

        SearchHits hits = response.getHits();

        for (SearchHit hit : hits.getHits()) {
            System.out.println(hit.getIndex());
            System.out.println(hit.getId());
            System.out.println(hit.getSourceAsMap());
        }
    }

    /**
     * 复杂索引的检索
     */
    @Test
    public void testSearchAggregation() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("users");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("name", "手机"));
        sourceBuilder.aggregation(AggregationBuilders.terms("buyBoundsAgg").field("buyBounds").size(10));
        sourceBuilder.aggregation(AggregationBuilders.avg("growBoundsAvg").field("growBounds"));
        // {"query":{"match":{"name":{"query":"手机","operator":"OR","prefix_length":0,"max_expansions":50,"fuzzy_transpositions":true,"lenient":false,"zero_terms_query":"NONE","auto_generate_synonyms_phrase_query":true,"boost":1.0}}},"aggregations":{"buyBoundsAgg":{"terms":{"field":"buyBounds","size":10,"min_doc_count":1,"shard_min_doc_count":0,"show_term_doc_count_error":false,"order":[{"_count":"desc"},{"_key":"asc"}]}},"growBoundsAvg":{"avg":{"field":"growBounds"}}}}{"took":385,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":4,"relation":"eq"},"max_score":0.5156582,"hits":[{"_index":"users","_type":"_doc","_id":"1","_score":0.5156582,"_source":{"buyBounds":100,"growBounds":100,"name":"手机","spuId":1}},{"_index":"users","_type":"_doc","_id":"2","_score":0.5156582,"_source":{"buyBounds":200,"growBounds":200,"name":"手机","spuId":2}},{"_index":"users","_type":"_doc","_id":"3","_score":0.5156582,"_source":{"buyBounds":300,"growBounds":300,"name":"手机","spuId":3}},{"_index":"users","_type":"_doc","_id":"5","_score":0.5156582,"_source":{"buyBounds":300,"growBounds":500,"name":"手机","spuId":5}}]},"aggregations":{"avg#growBoundsAvg":{"value":275.0},"lterms#buyBoundsAgg":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0,"buckets":[{"key":300,"doc_count":2},{"key":100,"doc_count":1},{"key":200,"doc_count":1}]}}}
        System.out.println(sourceBuilder.toString());

        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        // {"took":385,"timed_out":false,"_shards":{"total":1,"successful":1,"skipped":0,"failed":0},"hits":{"total":{"value":4,"relation":"eq"},"max_score":0.5156582,"hits":[{"_index":"users","_type":"_doc","_id":"1","_score":0.5156582,"_source":{"buyBounds":100,"growBounds":100,"name":"手机","spuId":1}},{"_index":"users","_type":"_doc","_id":"2","_score":0.5156582,"_source":{"buyBounds":200,"growBounds":200,"name":"手机","spuId":2}},{"_index":"users","_type":"_doc","_id":"3","_score":0.5156582,"_source":{"buyBounds":300,"growBounds":300,"name":"手机","spuId":3}},{"_index":"users","_type":"_doc","_id":"5","_score":0.5156582,"_source":{"buyBounds":300,"growBounds":500,"name":"手机","spuId":5}}]},"aggregations":{"avg#growBoundsAvg":{"value":275.0},"lterms#buyBoundsAgg":{"doc_count_error_upper_bound":0,"sum_other_doc_count":0,"buckets":[{"key":300,"doc_count":2},{"key":100,"doc_count":1},{"key":200,"doc_count":1}]}}}
        System.out.println(response.toString());

        SearchHits hits = response.getHits();
        for (SearchHit hit : hits.getHits()) {
            System.out.println(hit.getIndex());
            System.out.println(hit.getId());
            System.out.println(hit.getSourceAsMap());

            SpuBoundDTO spuBoundDTO = JSONObject.parseObject(hit.getSourceAsString(), SpuBoundDTO.class);
            System.out.println(spuBoundDTO.toString());
        }

        Aggregations aggregations = response.getAggregations();

        Terms terms = aggregations.get("buyBoundsAgg");
        for (Terms.Bucket bucket : terms.getBuckets()) {
            System.out.println("buyBoundsAgg: " + bucket.getKey() + ":" + bucket.getDocCount());
        }

        Avg avg = aggregations.get("growBoundsAvg");
        System.out.println("growBoundsAvg: " + avg.getValue());
    }



}
