package site.cnkj.util.service;

import lombok.Data;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;


/**
 * 当前模块主要用于SpringCloud监控服务的状态信息等
 */
@Component
@Data
public class CommonInfo implements InfoContributor {
    //服务当前状态
    private String CURRENT_STATUS = "vacancy";

    //繁忙，执行中
    public static final String BUSTLING = "bustling";
    //空闲
    public static final String VACANCY = "vacancy";

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("CURRENT_STATUS", CURRENT_STATUS);
    }

    public void clearAll() {
        this.CURRENT_STATUS = "vacancy";
    }


}
