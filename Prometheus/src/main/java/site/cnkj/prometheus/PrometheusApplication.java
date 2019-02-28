package site.cnkj.prometheus;

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/*
 * @version 1.0 created by LXW on 2019/2/26 14:53
 */
@EnablePrometheusEndpoint
@ComponentScan(basePackages = "site.cnkj.prometheus")
@SpringBootApplication
public class PrometheusApplication {

    public static void main(String[] args){
        SpringApplication.run(PrometheusApplication.class);
    }

}
