package site.cnkj.util.config;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.HashSet;
import java.util.Set;

/*
 * @version 1.0 created by LXW on 2019/6/24 13:53
 */
@Configuration
@ConditionalOnProperty(prefix = "spring.redis.sentinel", name = "nodes")
public class RedisSentinelConfig {

    @Value("${spring.redis.sentinel.nodes}")
    private String redisNodes = null;

    @Value("${spring.redis.sentinel.master}")
    private String master = null;

    @Value("${spring.redis.sentinel.masterName}")
    private String masterName = null;

    @Value("${spring.redis.sentinel.database}")
    private Integer database = 0;

    @Value("${spring.redis.sentinel.password}")
    private String password = "";

    /**
     * 配置redis的哨兵
     * @return RedisSentinelConfiguration
     * @throws
     */
    @Bean
    public RedisSentinelConfiguration sentinelConfiguration(){
        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
        //配置 master 的名称
        RedisNode masterRedisNode = new RedisNode(master.split(":")[0], Integer.valueOf(master.split(":")[1]));
        masterRedisNode.setName(masterName);
        redisSentinelConfiguration.master(masterRedisNode);
        //配置redis的哨兵sentinel
        if (redisNodes != null && redisNodes.length() > 0){
            Set<RedisNode> redisNodeSet = new HashSet<>();
            for (String redisNode : redisNodes.split(",")) {
                String host = redisNode.split(":")[0];
                Integer port = Integer.valueOf(redisNode.split(":")[1]);
                RedisNode senRedisNode = new RedisNode(host,port);
                redisNodeSet.add(senRedisNode);
            }
            redisSentinelConfiguration.setSentinels(redisNodeSet);
        }
        redisSentinelConfiguration.setDatabase(database);
        if (StringUtils.isNotEmpty(password) && password.length() > 0){
            redisSentinelConfiguration.setPassword(password);
        }
        return redisSentinelConfiguration;
    }
    /**
     * 配置工厂
     * @return
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisSentinelConfiguration sentinelConfig) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig);
        return jedisConnectionFactory;
    }
}
