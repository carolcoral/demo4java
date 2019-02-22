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
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import site.cnkj.es.config.ElasticConfig;
import site.cnkj.es.service.AsyncElasticsearchService;
import site.cnkj.es.service.ElasticSearchService;
import site.cnkj.util.CommonConstant;
import site.cnkj.util.DateUtil;
import site.cnkj.util.DiscoveryClientUtil;
import site.cnkj.util.RedisUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
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

    @Value("${elasticsearch.mustNotValue:''}")
    private String mustNotValue = "";

    private String TOPIC_NAME = "elasticSearch";

    final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(10L));


    /**
     * 查询es数据并发送到kafka
     *
     * @param index       索引
     * @param filterKey   关键字
     * @param filterValue 关键字的值
     * @param startTime   开始时间戳
     * @param endTime     结束时间戳
     * @return 执行状态
     */
    @Override
    public boolean queryDataToKafka(String index, String filterKey, String filterValue, String startTime, String endTime) {
        long start_time = DateUtil.getCurrentTime();
        Map map = new HashMap();
        map.put("index", index);
        map.put("filterKey", filterKey);
        map.put("filterValue", filterValue);
        map.put("period", startTime.concat("-").concat(endTime));
        map.put("topicName", TOPIC_NAME);
        //Timeout (seconds)
        int elasticsearchTimeout = Integer.parseInt(elasticConfig.getTimeout());
        //Single request quantity
        int elasticsearchSize = Integer.parseInt(elasticConfig.getSize());
        boolean flag = false;
        try {
            final Scroll scroll = new Scroll(TimeValue.timeValueMinutes(10L));
            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types("api");
            searchRequest.scroll(scroll);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            //Aggregate statement
            //重要,聚合查询语句
            searchSourceBuilder.query(QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchPhraseQuery(filterKey,filterValue))
                    .must(QueryBuilders.rangeQuery("@timestamp").gte(startTime).lte(endTime))
                    .mustNot(QueryBuilders.queryStringQuery(mustNotValue))
            )
                    .timeout(new TimeValue(elasticsearchTimeout,TimeUnit.SECONDS))
                    .size(elasticsearchSize);

            searchRequest.source(searchSourceBuilder);
            //Print the executed DSL statement, which can be used directly in Kibana
            //LOGGER.info("***************** 执行es查询的语句 *****************");
            //LOGGER.info("\n"+searchSourceBuilder.toString());
            SearchResponse searchResponse = client.search(searchRequest);
            if ("OK".equals(searchResponse.status().toString())){
                if (searchResponse.getHits().totalHits == 0){
                    //设置采集数量为 0
                    String info2 = "A total of [ 0 ] data was retrieved.";
                    LOGGER.info(info2);
                    flag = true;
                }else {
                    String scrollId = searchResponse.getScrollId();
                    SearchHit[] searchHits = searchResponse.getHits().getHits();
                    for (SearchHit hit : searchResponse.getHits().getHits()) {
                        asyncElasticsearchService.batchSendElasticDataToKafka(hit, TOPIC_NAME);
                    }
                    long totalHits = searchResponse.getHits().getTotalHits();
                    long length = searchResponse.getHits().getHits().length;
                    String info2 = "A total of [ " + totalHits + " ] data was retrieved, and the number of data processed [ " + length + " ] ";
                    LOGGER.info(info2);
                    //更新当前采集服务的采集总量
                    //monitorService.updateCurrentUriTotalNum(uid, String.valueOf(totalHits));
                    String completeTime = null;
                    List<CompletableFuture<Boolean>> completableFutureList = new ArrayList<CompletableFuture<Boolean>>();
                    while (searchHits != null && searchHits.length > 0) {
                        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                        scrollRequest.scroll(scroll);
                        searchResponse = client.searchScroll(scrollRequest);
                        CompletableFuture<Boolean> batchSearchElasticData = asyncElasticsearchService.batchSearchElasticData(searchResponse, TOPIC_NAME);
                        completableFutureList.add(batchSearchElasticData);
                        length += searchResponse.getHits().getHits().length;
                        String info3 = "A total of [ " + totalHits + " ] data was retrieved, and the number of data processed [ " + length + " ] ";
                        LOGGER.info(info3);
                        if (length == totalHits) {
                            break;
                        }
                    }
                    map.put("totalHits", String.valueOf(length));
                    CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();
                    for (CompletableFuture<Boolean> cf : completableFutureList) {
                        try {
                            if (cf.get()) {
                                completeTime = String.valueOf(DateUtil.getCurrentTime());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                    clearScrollRequest.addScrollId(scrollId);
                    ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest);
                    flag = clearScrollResponse.isSucceeded();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }finally {
            long end_time = DateUtil.getCurrentTime();
            LOGGER.warn(map+ " >>>>>>>>>> 当前查询总用时:"+ String.valueOf(end_time-start_time));
            return flag;
        }
    }

    @Override
    public boolean queryDataToKafkaByRedisPublish(String index, String filterKey, String filterValue, String startTime, String endTime) {
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
                    .must(QueryBuilders.matchPhraseQuery(filterKey,filterValue))
                    //.must(QueryBuilders.queryStringQuery(filterValue))
                    .must(QueryBuilders.rangeQuery("@timestamp").gte(startTime).lte(endTime))
            )
                    .timeout(new TimeValue(elasticSearchTimeout,TimeUnit.SECONDS))
                    .size(elasticSearchSize);
            //路由查询结果到同一个分片上
            searchRequest.source(searchSourceBuilder).routing(DiscoveryClientUtil.getLocalIP());
            //Print the executed DSL statement, which can be used directly in Kibana
            LOGGER.info(searchSourceBuilder.toString());
            SearchResponse searchResponse = client.search(searchRequest);
            if (searchResponse.getHits().totalHits == 0){
                String info2 = "A total of [ 0 ] data was retrieved.";
                LOGGER.info(info2);
                flag = true;
            }else {
                if ("OK".equals(searchResponse.status().toString())){
                    //非常值得深入研究下的分页标识，可以扩展成无缝扩容、无缝降压
                    String scrollId = searchResponse.getScrollId();
                    System.out.println("***********scrollId*************\n"+scrollId);
                    //将获取到的scrollId保存到redis以便其他扩展服务使用
                    redisUtil.publishMessage(CommonConstant.REDIS.ESScrollId, scrollId, true);
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

    /**
     * 通过订阅的方式使用scrollId进行分布式横向扩容查询会出现严重的数据丢失问题，不推荐使用
     * @param scrollId
     * @return
     */
    @Override
    public boolean queryDataToKafkaById(String scrollId) {
        boolean flag = true;
        try {
            List<CompletableFuture<Boolean>> completableFutureList = new ArrayList<CompletableFuture<Boolean>>();
            SearchResponse searchResponse = null;
            long totalHits = 0;
            long length = 0;

            //设置当前服务的采集状态
            redisUtil.hset("ScrollId_Code", DiscoveryClientUtil.getLocalIP(), "false");
            //等待随机毫秒（3位），避免重复数据的插入
            Thread.sleep((long) Math.ceil(Math.random()*1000));
            while(true){
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(scroll);
                searchResponse = client.searchScroll(scrollRequest);
                if (searchResponse.getHits().getHits().length == 0){
                    LOGGER.info("当前查询结果为空或已完成全部查询");
                    break;
                }
                totalHits = searchResponse.getHits().getTotalHits();
                CompletableFuture<Boolean> batchSearchElasticData = asyncElasticsearchService.batchSearchElasticData(searchResponse, TOPIC_NAME);
                completableFutureList.add(batchSearchElasticData);
                length += searchResponse.getHits().getHits().length;
                String info3 = "A total of [ " + totalHits + " ] data was retrieved, and the number of data processed [ " + length + " ] ";
                LOGGER.info(info3);
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
            redisUtil.hset("ScrollId_Code", DiscoveryClientUtil.getLocalIP(), "true");
            Map keyMaps = redisUtil.hmget("ScrollId_Code");
            Set set = new HashSet();
            for (Object key : keyMaps.keySet()){
                Object value = keyMaps.get(key);
                set.add(value);
            }
            boolean b = false;
            for (Object o : set) {
                if ("true".equals(o)){
                    b = Boolean.valueOf(o.toString());
                }
            }
            if (b){
                ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
                clearScrollRequest.addScrollId(scrollId);
                ClearScrollResponse clearScrollResponse = client.clearScroll(clearScrollRequest);
                flag = clearScrollResponse.isSucceeded();
            }
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
            redisUtil.hset("ScrollId_Code", DiscoveryClientUtil.getLocalIP(), "true");
        } finally {
            return flag;
        }
    }
}
