package site.cnkj.es.service.impl;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import site.cnkj.es.service.AsyncElasticsearchService;

/**
 * 主要执行ES查询和发送数据到kafka的功能
 */
@Service
public class AsyncElasticsearchServiceImpl implements AsyncElasticsearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncElasticsearchServiceImpl.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Async("taskExecutor")
    @Override
    public CompletableFuture batchSearchElasticData(SearchResponse searchResponse, String topicName) {
        try {
            List<CompletableFuture<Boolean>> completableFutureList = new ArrayList<CompletableFuture<Boolean>>();
            Boolean cuTime = true;
            //批量发送数据
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                //异步操作获取到的数据
                CompletableFuture<Boolean> batchSendElasticDataToKafka = batchSendElasticDataToKafka(hit, topicName);
                completableFutureList.add(batchSendElasticDataToKafka);
            }
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();
            for (CompletableFuture<Boolean> cf :completableFutureList){
                try {
                    if(cf.get()){
                        cuTime = true;
                    }else {
                        cuTime = false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return CompletableFuture.completedFuture(cuTime);
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error(e.toString());
        }
        return null;
    }


    @Async("taskExecutorElastic")
    @Override
    public CompletableFuture batchSendElasticDataToKafka(SearchHit searchHit, String topicName) {
        try {
            String res = searchHit.getSourceAsString();
            ListenableFuture re = kafkaTemplate.send(topicName, res);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
        }
        return null;
    }
}
