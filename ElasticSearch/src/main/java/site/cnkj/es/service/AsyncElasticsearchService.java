package site.cnkj.es.service;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.util.concurrent.CompletableFuture;


public interface AsyncElasticsearchService {


    /**
     * 异步批量查询es中的数据
     * @param searchResponse
     * @return
     */
    CompletableFuture batchSearchElasticData(SearchResponse searchResponse, String topicName);


    /**
     * 异步批量发送数据到kafka
     * @param searchHit
     * @return
     */
    CompletableFuture batchSendElasticDataToKafka(SearchHit searchHit, String topicName);

}
