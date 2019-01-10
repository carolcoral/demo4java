package site.cnkj.es.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.cnkj.es.service.ElasticSearchService;

/*
 * @version 1.0 created by LXW on 2019/1/9 16:17
 */
@Component
public class SubDescription{

    @Autowired
    private ElasticSearchService elasticSearchService;

    //是否开启订阅查询服务
    @Value("${spring.redis.subdescription}")
    private boolean subDescription;

    public boolean receiveMessage(String message){
        message = message.replaceAll("\"", "");
        boolean flag = false;
        if (subDescription){
            flag = elasticSearchService.queryDataToKafkaById(message);
        }
        return flag;
    }

}
