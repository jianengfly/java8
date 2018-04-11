package com.rograndec.jianeng.service;

import com.rograndec.jianeng.config.GlobalProperties;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service("redisService")
public class RedisService {
    private static final Logger logger = LoggerFactory.getLogger(RedisService.class);

    @Autowired
    @Resource(name = "jedisPool")
    JedisPool redisPool;

    String password = (String) GlobalProperties.getPropertyValue("spring.redis.password");

    public static final int DEFAULT_EXPIRE_TIME = 60 * 30;

    @SuppressWarnings("deprecation")
    @PostConstruct
    public void init () {
        try {
            Jedis jedis = redisPool.getResource();
            redisPool.returnResource(jedis);
        } catch (Throwable e) {

        }
    }

    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = redisPool.getResource();
            jedis.auth(password);
        } catch (Exception e) {
            ;
        }
        return jedis;
    }

    public void returnJedis (Jedis jedis) {
        redisPool.returnResource(jedis);
    }

    public void incrBy(String key, long value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            boolean exists = jedis.exists(key);
            jedis.incrBy(key, value);
            if (!exists) {
                returnJedis(jedis);
            }
        } catch (Exception e) {
            if (null != jedis) {
                returnJedis(jedis);
            }
        }
    }

    public String hget(String key, String field) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = getJedis();
            value = jedis.hget(key, field);
            returnJedis(jedis);
        } catch (Exception e) {
            if (null != jedis) {
                returnJedis(jedis);
            }
        }
        return value;
    }

    public void hset(String key, String field, String value) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            Long created = jedis.hset(key, field, value);
            if (created == 1L) {
                jedis.expire(key, DEFAULT_EXPIRE_TIME);
            }
            returnJedis(jedis);
        } catch (Exception e) {
            if (null != jedis) {
                returnJedis(jedis);
            }
        }
    }

    public long hincrby (String key, String field, long value) {
        Jedis jedis = null;
        Long afterChanged = 1L ;
        try {
            jedis = getJedis();
            boolean exists = jedis.exists(key);
            afterChanged = jedis.hincrBy(key, field, value);
            if (!exists) {
                jedis.expire(key, DEFAULT_EXPIRE_TIME);
            }
            returnJedis(jedis);
        } catch (Exception e) {
            if (null != jedis) {
                returnJedis(jedis);
            }
        }
    }

}
