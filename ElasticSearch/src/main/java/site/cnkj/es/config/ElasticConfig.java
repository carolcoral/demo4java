package site.cnkj.es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/*
 * @version 1.0 created by LXW on 2019/1/8 16:23
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "common.elasticsearch")
public class ElasticConfig {
    private String timeout;
    private String size;
}
