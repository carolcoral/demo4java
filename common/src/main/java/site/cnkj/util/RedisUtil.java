package site.cnkj.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedisUtil{

    private RedisTemplate<String, Object> redisTemplate;

    //设置的默认前缀，可以为空
    @Value("${spring.application.name}")
    private String redisName;


    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    //=============================common============================

    public void setRedisName(String redisName) {
        this.redisName = redisName;
    }

    ;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String service, String key, long time) {
        try {
            if (StringUtils.isEmpty(service)) {
                if (time > 0) {
                    redisTemplate.expire(redisName + ":" + key, time, TimeUnit.SECONDS);
                }
            } else {
                if (time > 0) {
                    redisTemplate.expire(redisName + ":" + service + ":" + key, time, TimeUnit.SECONDS);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(redisName + ":" + key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String service, String key) {
        return redisTemplate.getExpire(redisName + ":" + service + ":" + key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String service, String key) {
        try {
            return redisTemplate.hasKey(redisName + ":" + service + ":" + key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String service, String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(redisName + ":" + service + ":" + key[0]);
            } else {
                redisTemplate.delete(Arrays.stream(key).map(k -> redisName + ":" + service + ":" + k).collect(Collectors.toList()));
            }
        }
    }

    //============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String service, String key) {
        return redisName + ":" + service + ":" + key == null ? null : redisTemplate.opsForValue().get(redisName + ":" + service + ":" + key);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisName + ":" + key == null ? null : redisTemplate.opsForValue().get(redisName + ":" + key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String service, String key, Object value) {
        try {
            redisTemplate.opsForValue().set(redisName + ":" + service + ":" + key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * @param serviceName 服务名
     * @param logName     模版名
     * @param key         键
     * @param value       值
     * @return
     */
    public boolean set(String serviceName, String logName, String key, Object value) {
        try {
            redisTemplate.opsForValue().set(serviceName + ":" + logName + ":" + key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(redisName + ":" + key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String service, String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(redisName + ":" + service + ":" + key, value, time, TimeUnit.SECONDS);
            } else {
                set(service, key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(redisName + ":" + key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String service, String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(redisName + ":" + service + ":" + key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String service, String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(redisName + ":" + service + ":" + key, -delta);
    }

    //================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String service, String key, String item) {
        if (StringUtils.isEmpty(service)) {
            return redisTemplate.opsForHash().get(redisName + ":" + key, item);
        } else {
            return redisTemplate.opsForHash().get(redisName + ":" + service + ":" + key, item);
        }
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(redisName + ":" + key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String service, String key) {
        return redisTemplate.opsForHash().entries(redisName + ":" + service + ":" + key);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(redisName + ":" + key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String service, String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(redisName + ":" + service + ":" + key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String service, String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(redisName + ":" + service + ":" + key, map);
            if (time > 0) {
                expire(service, key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String service, String key, String item, Object value) {
        try {
            if (StringUtils.isEmpty(service)) {
                redisTemplate.opsForHash().put(redisName + ":" + key, item, value);
            } else {
                redisTemplate.opsForHash().put(redisName + ":" + service + ":" + key, item, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hset(String service, String key, String item, long time) {
        try {
            redisTemplate.opsForHash().put(redisName + ":" + service, key, item);
            if (time > 0) {
                expire(redisName + ":" + service, key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value) {
        redisTemplate.opsForHash().put(redisName + ":" + key, item, value);
        return true;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String service, String key, String item, Object value, long time) {
        try {
            if (StringUtils.isEmpty(service)) {
                redisTemplate.opsForHash().put(redisName + ":" + key, item, value);
                if (time > 0) {
                    expire(service, key, time);
                }
            } else {
                redisTemplate.opsForHash().put(redisName + ":" + service + ":" + key, item, value);
                if (time > 0) {
                    expire(service, key, time);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String service, String key, Object... item) {
        redisTemplate.opsForHash().delete(redisName + ":" + service + ":" + key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String service, String key, String item) {
        return redisTemplate.opsForHash().hasKey(redisName + ":" + service + ":" + key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public double hincr(String service, String key, String item, double by) {
        return redisTemplate.opsForHash().increment(redisName + ":" + service + ":" + key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public double hdecr(String service, String key, String item, double by) {
        return redisTemplate.opsForHash().increment(redisName + ":" + service + ":" + key, item, -by);
    }

    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String service, String key) {
        try {
            return redisTemplate.opsForSet().members(redisName + ":" + service + ":" + key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String service, String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(redisName + ":" + service + ":" + key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String service, String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(redisName + ":" + service + ":" + key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String service, String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(redisName + ":" + service + ":" + key, values);
            if (time > 0) expire(service, key, time);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String service, String key) {
        try {
            return redisTemplate.opsForSet().size(redisName + ":" + service + ":" + key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String service, String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(redisName + ":" + service + ":" + key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束  0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String service, String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(redisName + ":" + service + ":" + key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String service, String key) {
        try {
            return redisTemplate.opsForList().size(redisName + ":" + service + ":" + key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String service, String key, long index) {
        try {
            return redisTemplate.opsForList().index(redisName + ":" + service + ":" + key, index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String service, String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(redisName + ":" + service + ":" + key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String service, String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(redisName + ":" + service + ":" + key, value);
            if (time > 0) expire(service, key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String service, String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(redisName + ":" + service + ":" + key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String service, String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(redisName + ":" + service + ":" + key, value);
            if (time > 0) expire(service, key, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String service, String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(redisName + ":" + service + ":" + key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String service, String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(redisName + ":" + service + ":" + key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //============================StringRedisTemplate=============================

    /**
     * 查询某个前缀的全部key
     *
     * @param key
     * @return
     */
    public List<String> getKeys(String key) {
        try {
            Set<String> keySet = new HashSet();
            List<String> keyList = new ArrayList<>();
            keySet = redisTemplate.keys(redisName.concat(":").concat(key).concat("*"));
            for (String s : keySet) {
                keyList.add(s);
            }
            return keyList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前redis中全部的key
     *
     * @return
     */
    public List<String> getKeys() {
        try {
            Set<String> keySet = new HashSet();
            List<String> keyList = new ArrayList<>();
            keySet = redisTemplate.keys(redisName.concat("*"));
            for (String s : keySet) {
                keyList.add(s);
            }
            return keyList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //-----------------------------redis消息发布------------------------------

    /**
     * 消息发布
     * @param channel 通道名
     * @param message 信息
     */
    public void releaseMessage(String channel, String message, boolean needServiceName){
        try {
            String channelName = new String();
            if (needServiceName){
                channelName = redisName + ":" + channel;
            }else {
                channelName = channel;
            }
            redisTemplate.convertAndSend(channelName, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}