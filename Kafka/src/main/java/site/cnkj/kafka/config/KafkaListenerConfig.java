package site.cnkj.kafka.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import site.cnkj.kafka.service.Listener;
import site.cnkj.util.FileOperationUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Configuration
public class KafkaListenerConfig implements BeanDefinitionRegistryPostProcessor , PriorityOrdered {


    private List<String> listernerlist;

    KafkaListenerConfig() throws IOException {
        Properties pro = new Properties();
        File f;
        String osVersion = System.getProperty("os.name").toUpperCase();
        if (osVersion.contains("WINDOWS")) {
            //f =new File(this.getClass().getResource("/template.properties").getPath());
            f = FileOperationUtil.getPropertiesFileForWin();
        }else {
            f = new File(Paths.get(System.getProperty("user.dir"), "config", "template.properties").toString());
        }
        FileInputStream in = new FileInputStream(f);
        pro.load(in);
        in.close();
        String listString = pro.getProperty("template.listernerlist");
        listernerlist = Arrays.asList(listString.split(","));
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for (String listener :  listernerlist) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(Listener.class);

            builder.addConstructorArgValue(listener);
            beanDefinitionRegistry.registerBeanDefinition(listener, builder.getBeanDefinition());
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
