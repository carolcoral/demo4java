package site.cnkj.es.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.cnkj.es.service.ElasticSearchService;
import site.cnkj.util.config.SubDescription;

/*
 * @version 1.0 created by LXW on 2019/1/15 15:36
 */
@Component
public class SubDescriptionImpl implements SubDescription {

    @Value("${spring.redis.subdescription}")
    private boolean flag = false;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Override
    public Object receiveMessage(String message){
        message = message.replaceAll("\"", "");
        boolean code = true;
        if (flag == true){
            code = elasticSearchService.queryDataToKafkaById(message);
        }
        return code;
    }

}
