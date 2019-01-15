package site.cnkj.es.service.impl;

import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import site.cnkj.es.config.ElasticConfig;
import site.cnkj.es.service.AsyncElasticsearchService;
import site.cnkj.es.service.ElasticSearchService;
import site.cnkj.util.CommonConstant;
import site.cnkj.util.RedisUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/*
 * @version 1.0 created by LXW on 2019/1/8 16:30
 */
@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private AsyncElasticsearchService asyncElasticsearchService;

    @Autowired
    private ElasticConfig elasticConfig;

    @Autowired
    private RedisUtil redisUtil;

    @Resource(name = "HighLevelClient")
    private RestHighLevelClient client;

    private String TOPIC_NAME = "elasticSearch";

    final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(10L));


    @Override
    public boolean queryDataToKafka(String index, String filterKey, String filterValue, String startTime, String endTime) {
        boolean flag = false;
        int elasticSearchTimeout = Integer.parseInt(elasticConfig.getTimeout());
        int elasticSearchSize = Integer.parseInt(elasticConfig.getSize());
        try {
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.scroll(scroll);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //Aggregate statement
            //重要,聚合查询语句
            searchSourceBuilder.query(QueryBuilders.boolQuery()
                    //.must(QueryBuilders.matchPhraseQuery(filterKey,filterValue))
                    .must(QueryBuilders.queryStringQuery(filterValue))
                    .must(QueryBuilders.rangeQuery("@timestamp").gte(startTime).lte(endTime))
            )
                    .timeout(new TimeValue(elasticSearchTimeout,TimeUnit.SECONDS))
                    .size(elasticSearchSize);
            searchRequest.source(searchSourceBuilder);
            //Print the executed DSL statement, which can be used directly in Kibana
            //LOGGER.info(searchSourceBuilder.toString());
            SearchResponse searchResponse = client.search(searchRequest);
            if (searchResponse.getHits().totalHits == 0){
                String info2 = "A total of [ 0 ] data was retrieved.";
                LOGGER.info(info2);
                flag = true;
            }else {
                if ("OK".equals(searchResponse.status().toString())){
                    //非常值得深入研究下的分页标识，可以扩展成无缝扩容、无缝降压
                    String scrollId = searchResponse.getScrollId();
                    //将获取到的scrollId保存到redis以便其他扩展服务使用
                    redisUtil.releaseMessage(CommonConstant.REDIS.ESScrollId, scrollId, false);
                    SearchHit[] searchHits = searchResponse.getHits().getHits();
                    //当执行分页查询的时候第一次查询的数据将不再重复查询，所以为了数据完整性，第一次查询需要单独处理
                    for (SearchHit hit : searchResponse.getHits().getHits()) {
                        asyncElasticsearchService.batchSendElasticDataToKafka(hit, TOPIC_NAME);
                    }
                    long totalHits = searchResponse.getHits().getTotalHits();
                    long length = searchResponse.getHits().getHits().length;
                    String info2 = "A total of [ " + totalHits + " ] data was retrieved, and the number of data processed [ " + length + " ] ";
                    LOGGER.info(info2);
                    flag = true;
                    // 严重警告
                    // 在该使用聚合查询的方法里面如果希望多个服务使用相同的scrollId进行查询一定不能在这里调用分页查询功能，否则数据会出现混乱以及严重丢失数据的情况
                }else {
                    flag = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }finally {
            return flag;
        }
    }

    @Override
    public boolean queryDataToKafkaById(String scrollId) {
        boolean flag = false;
        try {
            List<CompletableFuture<Boolean>> completableFutureList = new ArrayList<CompletableFuture<Boolean>>();
            SearchResponse searchResponse = null;
            long totalHits = 0;
            long length = 0;
            while(true){
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.searchScroll(scrollRequest);
                totalHits = searchResponse.getHits().getTotalHits();
                CompletableFuture<Boolean> batchSearchElasticData = asyncElasticsearchService.batchSearchElasticData(searchResponse, TOPIC_NAME);
                completableFutureList.add(batchSearchElasticData);
                length += searchResponse.getHits().getHits().length;
                String info3 = "A total of [ " + totalHits + " ] data was retrieved, and the number of data processed [ " + length + " ] ";
                LOGGER.info(info3);
                if (searchResponse == null || searchResponse.getHits().getHits().length == 0 || searchResponse.getHits().getHits() == null){
                    break;
                }
            }
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();
            for (CompletableFuture<Boolean> cp :completableFutureList){
                try {
                    if(!cp.get()){
                        flag = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.addScrollId(scrollId);
            ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest);
            flag = clearScrollResponse.isSucceeded();
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            return flag;
        }
    }
}