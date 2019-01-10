package site.cnkj.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.SmartLifecycle;

import javax.annotation.Resource;


public abstract class DependenceCheckUtil implements SmartLifecycle {

    private boolean isRunning = false;

    @Autowired
    public DiscoveryClient discoveryClient;

    @Resource(name = "redisUtil")
    public RedisUtil redisUtil;

    @Resource(name = "restTemplateUtil")
    public RestTemplateUtil restTemplateUtil;

    @Value("${spring.application.name}")
    public String applicationName;

    //是否开启依赖检查
    @Value("${dependence.check}")
    public Boolean dependence;

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable runnable) {
        runnable.run();
        isRunning = false;
    }

    /**
     * 在你需要的服务中重写该方法，定制检查逻辑
     */
    @Override
    public abstract void start();

    @Override
    public void stop() {
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return 1;
    }


}
