# demo4java

## How to use

### Redis
* 1.If you want to use redis by single, you should impoert the "RedisConfg" only.

```
@SpringBootApplication
@ComponentScan(basePackages = "site.cnkj.*",
        basePackageClasses = {
                site.cnkj.util.config.RedisConfig.class
        })
public class CnkjApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CnkjApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Resource(name="redisUtil")
    public RedisUtil redisUtil;

}
```

> Then you can autowire it where you use the redisUtil.

```
@Autowired
private RedisUtil redisUtil;
```

* 2.If you want to use redis by sentinel, you should impoert the "RedisConfg" and "RedisSentinelConfig".

```
@SpringBootApplication
@ComponentScan(basePackages = "site.cnkj.*",
        basePackageClasses = {
                site.cnkj.util.config.RedisConfig.class,
                site.cnkj.util.config.RedisSentinelConfig.class
        })
public class CnkjApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CnkjApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Resource(name="redisUtil")
    public RedisUtil redisUtil;

}
```

> Then you can autowire it where you use the redisUtil.

```
@Autowired
private RedisUtil redisUtil;
```

* 3.If you want to use redis by cluster, you should impoert the "RedisConfg" and "RedisClusterConfig".

```
@SpringBootApplication
@ComponentScan(basePackages = "site.cnkj.*",
        basePackageClasses = {
                site.cnkj.util.config.RedisConfig.class,
                site.cnkj.util.config.RedisClusterConfig.class
        })
public class CnkjApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(CnkjApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Resource(name="redisUtil")
    public RedisUtil redisUtil;

}
```

> Then you can autowire it where you use the redisUtil.

```
@Autowired
private RedisUtil redisUtil;
```
