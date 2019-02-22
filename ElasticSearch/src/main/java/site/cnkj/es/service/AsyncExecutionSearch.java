package site.cnkj.es.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import site.cnkj.util.CommonConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/*
 * @version 1.0 created by LXW on 2019/2/21 14:59
 */
@Service
public class AsyncExecutionSearch {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncExecutionSearch.class);

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Async("taskExecutor")
    public CompletableFuture getAndSendData(String index, String filterKey , String filterValue, String startTime, String endTime) {
        try {
            long start = System.currentTimeMillis();
            String startMsg = "开始执行 " + filterValue + " 任务，开始时间: " + String.valueOf(start);
            LOGGER.info(startMsg);
            //主要执行采集任务的地方
            Boolean result = elasticSearchService.queryDataToKafka(index, filterKey, filterValue, startTime, endTime);
            long end = System.currentTimeMillis();
            //获取执行时间
            String executionTime = String.valueOf((end-start));
            LOGGER.info("总执行时间>>>>>>>>>>>>> " + executionTime + " 毫秒");
            return CompletableFuture.completedFuture(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
