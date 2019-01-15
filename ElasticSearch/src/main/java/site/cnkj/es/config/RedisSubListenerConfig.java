package site.cnkj.es.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import site.cnkj.util.CommonConstant;

import java.util.List;

/*
 * @version 1.0 created by LXW on 2019/1/9 15:34
 */
@Configuration
public class RedisSubListenerConfig {
    
    String channel = "";

    public RedisSubListenerConfig(@Value("${spring.application.name}") String name) {
        this.channel = name + ":" + CommonConstant.REDIS.ESScrollId;
    }

    /**
     * redis消息监听器容器
     * 可以添加多个监听不同话题的redis监听器，只需要把消息监听器和相应的消息订阅处理器绑定，该消息监听器
     * 通过反射技术调用消息订阅处理器的相关方法进行一些业务处理
     * 订阅多少个通道就增加多少个 MessageListenerAdapter
     * @param connectionFactory
     * @param listenerAdapter
     * @return
     */
    @Bean
    //相当于xml中的bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        //订阅了 channel 的通道
        //订阅的每一个通道都需要使用下面的语句新增
        container.addMessageListener(listenerAdapter, new PatternTopic(channel));
        return container;
    }

    /**
     * 消息监听器适配器，绑定消息处理器，利用反射技术调用消息处理器的业务方法
     * 需要做多少个消息通信就新建多少个bean并且绑定不同的defaultListenerMethod，可以使用同一个component
     * @param subDescription
     * @return
     */
    @Bean
    MessageListenerAdapter listenerAdapter(SubDescription subDescription) {
        return new MessageListenerAdapter(subDescription, "receiveMessage");
    }

    /**redis 读取内容的template */
    @Bean
    StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

}
