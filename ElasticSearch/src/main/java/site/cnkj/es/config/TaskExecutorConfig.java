package site.cnkj.es.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/*
 * @version 1.0 created by liuxuewen on 2018/9/14 10:22
 */
@Data
@EnableAsync
@Configuration
public class TaskExecutorConfig {

    @Value("${thread.taskexecutor.corePoolSize}")
    private String CorePoolSize;

    @Value("${thread.taskexecutor.maxPoolSize}")
    private String MaxPoolSize;

    @Value("${thread.taskexecutor.queueCapacity}")
    private String QueueCapacity;

    @Value("${thread.taskexecutor.keepaliveseconds}")
    private String KeepAliveSeconds;

    private String ThreadNamePrefix;

    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(CorePoolSize));
        executor.setMaxPoolSize(Integer.parseInt(MaxPoolSize));
        executor.setQueueCapacity(Integer.parseInt(QueueCapacity));
        executor.setKeepAliveSeconds(Integer.parseInt(KeepAliveSeconds));
        executor.setThreadNamePrefix(ThreadNamePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @Bean("taskExecutorElastic")
    public Executor taskExecutorElastic() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Integer.parseInt(CorePoolSize)*100);
        executor.setMaxPoolSize(Integer.parseInt(MaxPoolSize)*100);
        executor.setQueueCapacity(Integer.parseInt(QueueCapacity)*100);
        executor.setKeepAliveSeconds(Integer.parseInt(KeepAliveSeconds)*100);
        executor.setThreadNamePrefix("Get_Data_From_ES");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}
