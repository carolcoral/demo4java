package site.cnkj.es.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/*
 * @version 1.0 created by liuxuewen on 2018/10/16 17:19
 */
@Data
@Configuration
@PropertySource(value = {"classpath:collection.properties"})
@ConfigurationProperties(prefix = "collection")
public class FiltersConfig {

    private Map fileName;

    /** 00048 **/ private Map<String, Map<String,String>> a_10000_00048_;

    /** 00050 **/ private Map<String, Map<String,String>> a_10000_00050_;

    /** 00051 **/ private Map<String, Map<String,String>> a_10000_00051_;

    /** 00052 **/ private Map<String, Map<String,String>> a_10000_00052_;

    /** 00053 **/ private Map<String, Map<String,String>> a_10000_00053_;

    /** 00054 **/ private Map<String, Map<String,String>> a_10000_00054_;

    /** 00057 **/ private Map<String, Map<String,String>> a_10000_00057_;

    /** 00058 **/ private Map<String, Map<String,String>> a_10000_00058_;

    /** 00059 **/ private Map<String, Map<String,String>> a_10000_00059_;

    /** 00060 **/ private Map<String, Map<String,String>> a_10000_00060_;

    /** 00101 **/ private Map<String, Map<String,String>> a_10000_00101_;

    /** 00108 **/ private Map<String, Map<String,String>> a_10000_00108_;

    /** 00109 **/ private Map<String, Map<String,String>> a_10000_00109_;

    /** 00110 **/ private Map<String, Map<String,String>> a_10000_00110_;

}
