package site.cnkj.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/*
 * @version 1.0 created by LXW on 2019/1/16 14:44
 */
@ComponentScan(basePackages = "site.cnkj.kafka.*")
@EnableAsync
@SpringBootApplication
public class KafkaApplicationBoot {

    public static void main(String[] args) {
        SpringApplication.run(KafkaApplicationBoot.class, args);
    }

}
