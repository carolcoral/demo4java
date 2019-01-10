package site.cnkj.es.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import site.cnkj.es.service.ElasticSearchService;
import site.cnkj.util.RedisUtil;
import site.cnkj.util.domain.BaseResult;

/*
 * @version 1.0 created by LXW on 2019/1/8 17:27
 */
@RestController
public class ElasticSearchController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @RequestMapping(value = "/start",method = RequestMethod.POST)
    public BaseResult startQuery(String startTime, String endTime){
        BaseResult baseResult = new BaseResult();
        String index = "t3_java_monitor-*";
        String filterKey = "";
        String filterValue = "020010";
        System.out.println(elasticSearchService.queryDataToKafka(index, filterKey, filterValue, startTime, endTime));
        baseResult.setCode("000000");
        baseResult.setDesc("success");
        return baseResult;
    }

    @RequestMapping(value = "/channel",method = RequestMethod.POST)
    public void releaseMessage(String channel, String message){
        redisUtil.releaseMessage(channel, message, true);
    }

}
