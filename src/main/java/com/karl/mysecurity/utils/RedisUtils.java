package com.karl.mysecurity.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtils.class);
    private static RedisTemplate<String, Object> redisTemplate = null;

    @Autowired
    public RedisUtils(RedisTemplate<String, Object> redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    };

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public static boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }


    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public static void expire(String key, long time) {
        try {
            redisTemplate.expire(key, time, TimeUnit.MINUTES);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    /**
     * 获取 Keys
     *
     * @param key 键
     */
    public static Set<String> keys(String key) {
        Set<String> result = new HashSet<>();
        try {
            result = redisTemplate.keys(key);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return result;
    }


    /**
     * 删除缓存
     */
    public static boolean del(String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 删除缓存
     */
    public static boolean del(Collection<String> keys) {
        try {
            redisTemplate.delete(keys);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }


    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        ValueOperations<String, T> operation = (ValueOperations<String, T>) redisTemplate.opsForValue();
        return operation.get(key);
    }

    public static <T> boolean set(String key, T value) {
        try {

            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception ex) {
           logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(分种) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public static boolean set(String key, Object value, long time) {
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.MINUTES);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }



    public static <T> T hget(String key, String hashKey) {
        HashOperations<String, String, T> hash = redisTemplate.opsForHash();
        return hash.get(key, hashKey);
    }

    public static <T> Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }


    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String hashKey, Object value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        return true;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key     键
     * @param hashKey 项
     * @param value   值
     * @param time    时间(分钟) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public static boolean hset(String key, String hashKey, Object value, long time) {
        redisTemplate.opsForHash().put(key, hashKey, value);
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.MINUTES);
        }
        return true;
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public static boolean hmset(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
        return true;
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public static boolean hmset(String key, Map<String, Object> map, long time) {
        redisTemplate.opsForHash().putAll(key, map);
        if (time > 0) {
            redisTemplate.expire(key, time, TimeUnit.MINUTES);
        }
        return true;
    }


    /**
     * 删除hash表中的值
     *
     * @param key     键 不能为null
     * @param hashKey 项 可以使多个 不能为null
     */
    public static void hdel(String key, Object... hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key     键 不能为null
     * @param hashKey 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }


    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param hashKey 项
     * @param by   要增加几(大于0)
     */
    public static double hincr(String key, String hashKey, double by) {
        return redisTemplate.opsForHash().increment(key, hashKey, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param hashKey 项
     * @param by   要减少记(小于0)
     */
    public static double hdecr(String key, String hashKey, double by) {
        return redisTemplate.opsForHash().increment(key, hashKey, -by);
    }


    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束 0 到 -1代表所有值
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> lrange(String key, long start, long end) {
        return (List<T>) redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     */
    public static long llen(String key) {
        Long l = redisTemplate.opsForList().size(key);
        if(l==null){
            return 0;
        }
        return l;
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public static Object lindex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 将一个或多个值插入到列表的尾部(最右边)。
     * @param key 键
     * @param value 值
     */
    public static boolean lrpush(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
        return true;
    }

    /**
     * 将一个或多个值插入到列表的尾部(最右边)。
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     */
    public static boolean lrpush(String key, Object value, long time) {
        redisTemplate.opsForList().rightPush(key, value);
        if (time > 0) {
            expire(key, time);
        }
        return true;
    }

    /**
     * 将一个或多个值插入到列表的尾部(最右边)。
     * @param key 键
     * @param value 值
     */
    public static boolean lrpush(String key, List<Object> value) {
        redisTemplate.opsForList().rightPushAll(key, value);
        return true;
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     */
    public static boolean lrpush(String key, List<Object> value, long time) {
        redisTemplate.opsForList().rightPushAll(key, value);
        if (time > 0) {
            expire(key, time);
        }
        return true;
    }

    /**
     * 通过索引来设置元素的值
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return /
     */
    public static boolean lset(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
        return true;
    }

    /**
     * 删除指定key集合中值等于value的元素(count=0, 删除所有值等于value的元素; count>0, 从头部开始删除第一个值等于value的元素; count<0, 从尾部开始删除第一个值等于value的元素)
     * @param key 键
     * @param count  count>0, 从头部开始删除第一个值等于value的元素; count<0, 从尾部开始删除第一个值等于value的元素
     * @param value 值
     * @return 移除的个数
     */
    public static long lremove(String key, long count, Object value) {
        Long l= redisTemplate.opsForList().remove(key, count, value);
        if(l==null){
            return 0;
        }
        return l;
    }

}
