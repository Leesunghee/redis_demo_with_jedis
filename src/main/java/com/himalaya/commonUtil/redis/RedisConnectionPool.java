package com.himalaya.commonUtil.redis;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class RedisConnectionPool {

    private static final int DEFAULT_REDIS_MAX_TOTAL = 100;
    private static final int DEFAULT_REDIS_MAX_IDLE = 100;
    private static final int DEFAULT_REDIS_MIN_IDLE = 10;

    private static JedisPool jedisPool;
    private static JedisCluster jedisCluster;

    static {
        Set<HostAndPort> redisClusterNodes =new HashSet<>();
        redisClusterNodes.add(new HostAndPort("x.x.x.x",6379));
        redisClusterNodes.add(new HostAndPort("x.x.x.x",6380));
        redisClusterNodes.add(new HostAndPort("x.x.x.x",6381));

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(DEFAULT_REDIS_MAX_TOTAL);
        jedisPoolConfig.setMaxIdle(DEFAULT_REDIS_MAX_IDLE);
        jedisPoolConfig.setMinIdle(DEFAULT_REDIS_MIN_IDLE);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestOnReturn(true);

        jedisCluster = new JedisCluster(redisClusterNodes, 1000, 1000, 5, "password", jedisPoolConfig);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (jedisCluster != null) {
                    jedisCluster.close();
                }
            }
        });
    }

    private RedisConnectionPool() {
    }

    public static JedisCluster getJedisCluster() {
        return jedisCluster;
    }
}
