package site.cnkj.es.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/*
 * @version 1.0 created by liuxuewen on 2018/10/17 12:34
 */
@Data
@Configuration
public class CollectionConfig {

    @Autowired
    private FiltersConfig filtersConfig;

    private String key;

    public String fileName;

    public String getFileName() {
        Map map = filtersConfig.getFileName();
        return map.get(key).toString();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private Map<String, Map<String,String>> filters;

    public Map<String, Map<String, String>> getFilters() {
        Map<String, Map<String,String>> filters = null;
        if ("a_10000_00048_".equals(key)){
            //00048
            filters = filtersConfig.getA_10000_00048_();
        }else if ("a_10000_00050_".equals(key)){
            //00050
            filters = filtersConfig.getA_10000_00050_();
        }else if ("a_10000_00051_".equals(key)){
            //00051
            filters = filtersConfig.getA_10000_00051_();
        }else if ("a_10000_00052_".equals(key)){
            //00052
            filters = filtersConfig.getA_10000_00052_();
        }else if ("a_10000_00053_".equals(key)){
            //00053
            filters = filtersConfig.getA_10000_00053_();
        }else if ("a_10000_00054_".equals(key)){
            //00054
            filters = filtersConfig.getA_10000_00054_();
        }else if ("a_10000_00057_".equals(key)){
            //00057
            filters = filtersConfig.getA_10000_00057_();
        }else if ("a_10000_00058_".equals(key)){
            //00058
            filters = filtersConfig.getA_10000_00058_();
        }else if ("a_10000_00059_".equals(key)){
            //00059
            filters = filtersConfig.getA_10000_00059_();
        }else if ("a_10000_00060_".equals(key)){
            //00060
            filters = filtersConfig.getA_10000_00060_();
        }else if ("a_10000_00101_".equals(key)){
            //00101
            filters = filtersConfig.getA_10000_00101_();
        }else if ("a_10000_00108_".equals(key)){
            //00101
            filters = filtersConfig.getA_10000_00108_();
        } else if ("a_10000_00109_".equals(key)){
            //00101
            filters = filtersConfig.getA_10000_00109_();
        } else if ("a_10000_00110_".equals(key)){
            //00101
            filters = filtersConfig.getA_10000_00110_();
        }
        return filters;
    }

    public void setFilters(Map<String, Map<String, String>> filters) {
        this.filters = filters;
    }
}
