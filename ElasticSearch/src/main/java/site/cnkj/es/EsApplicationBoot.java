package site.cnkj.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import site.cnkj.util.RedisUtil;
import site.cnkj.util.config.ElasticsearchConfig;
import site.cnkj.util.config.RedisConfig;
import site.cnkj.util.config.RestTemplateConfig;

import javax.annotation.Resource;

/*
 * @version 1.0 created by LXW on 2019/1/8 14:45
 */
@ComponentScan(basePackages = "site.cnkj.es.*",basePackageClasses = {
        ElasticsearchConfig.class,
        RestTemplateConfig.class,
        RedisConfig.class
})
@EnableAsync
@SpringBootApplication
public class EsApplicationBoot {

    public static void main(String[] args) {
        SpringApplication.run(EsApplicationBoot.class, args);
    }

    @Resource(name = "redisUtil")
    private RedisUtil redisUtil;

}
