package site.cnkj.util.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.HashSet;
import java.util.Set;

/*
 * @version 1.0 created by LXW on 2019/6/25 9:40
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.redis.cluster", name = "nodes")
public class RedisClusterConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${spring.redis.cluster.password}")
    private String password = "";

    @Value("${spring.redis.cluster.maxRedirects}")
    private Integer maxRedirects = 5;

    /**
     * Redis集群的配置
     * @return RedisClusterConfiguration
     * @throws
     */
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration(){
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        //Set<RedisNode> clusterNodes
        String[] serverArray = clusterNodes.split(",");
        Set<RedisNode> nodes = new HashSet<RedisNode>();
        for(String ipPort:serverArray){
            String[] ipAndPort = ipPort.split(":");
            nodes.add(new RedisNode(ipAndPort[0].trim(),Integer.valueOf(ipAndPort[1])));
        }
        redisClusterConfiguration.setClusterNodes(nodes);
        //一条命令在集群中的转发次数
        redisClusterConfiguration.setMaxRedirects(maxRedirects);
        if (password.length() > 0){
            redisClusterConfiguration.setPassword(password);
        }
        return redisClusterConfiguration;
    }
    /**
     * 配置工厂
     * @param redisClusterConfiguration
     * @return JedisConnectionFactory
     * @throws
     */
    @Bean
    public JedisConnectionFactory JedisConnectionFactory(RedisClusterConfiguration redisClusterConfiguration){
        JedisConnectionFactory JedisConnectionFactory = new JedisConnectionFactory(redisClusterConfiguration);
        return JedisConnectionFactory;
    }

}
