package site.cnkj.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/*
 * @version 1.0 created by LXW on 2019/1/9 9:52
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplicationBoot {

    public static void main(String[] args) {
        SpringApplication.run(EurekaApplicationBoot.class, args);
    }

}
