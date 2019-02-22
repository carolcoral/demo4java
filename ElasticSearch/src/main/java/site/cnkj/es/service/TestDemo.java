package site.cnkj.es.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import site.cnkj.es.config.FiltersConfig;
import site.cnkj.util.CommonConstant;
import site.cnkj.util.DateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/*
 * @version 1.0 created by LXW on 2019/2/21 15:07
 */
@Service
public class TestDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDemo.class);

    @Autowired
    private FiltersConfig filtersConfig;

    @Autowired
    private AsyncExecutionSearch asyncExecutionSearch;

    public void start51(String startTime, String endTime) {
        long start_time = DateUtil.getCurrentTime();
        try {
            List<CompletableFuture<Boolean>> completableFutureList = new ArrayList<CompletableFuture<Boolean>>();
            Map<String,Map<String,String>> mp = filtersConfig.getA_10000_00051_();
            for (Map.Entry<String, Map<String,String>> e: mp.entrySet()){
                CompletableFuture<Boolean> collectionFlagFuture = asyncExecutionSearch.getAndSendData(
                        e.getValue().get("filterIndex"),
                        e.getValue().get("filterKey"),
                        e.getValue().get("filterValue"),
                        startTime,
                        endTime
                );
                completableFutureList.add(collectionFlagFuture);
            }
            CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[completableFutureList.size()])).join();
            Boolean asyncFlag = true;
            for (CompletableFuture<Boolean> cf :completableFutureList){
                try {
                    if(cf.get() && asyncFlag){
                        asyncFlag = true;
                    }else {
                        asyncFlag = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            long end_time = DateUtil.getCurrentTime();
            LOGGER.info("执行00051一共用时 >>>>>>>>>>>>>>>>>>>>>> "+String.valueOf((end_time-start_time)) + " 毫秒");
        }
    }

}
