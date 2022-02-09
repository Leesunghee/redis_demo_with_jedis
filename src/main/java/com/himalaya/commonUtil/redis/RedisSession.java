package com.himalaya.commonUtil.redis;

import redis.clients.jedis.JedisCluster;

import java.io.*;
import java.util.Base64;

public class RedisSession {

    private final static int DEFAULT_SESSION_INTERVAL = 1800;  // 30 minutes
    private final static String REDIS_CREATION_TIME = "creationTime";

    private RedisSession() {
    }

    /**
     * @Description 세션 유효성 확인
     * @param sessionId
     * @return
     */
    public static Boolean isValid(final String sessionId) throws Exception {
        JedisCluster jedisCluster =  RedisConnectionPool.getJedisCluster();
        boolean isValid = jedisCluster.exists(sessionIdToBase64Encode(sessionId));
        
        return isValid;
    }

    /**
     * @Description 세션 이름속성으로 값 가져오기
     * @param sessionId
     * @param name
     * @param <T>
     * @return
     */
    public static <T extends Serializable> T getAttribute(final String sessionId, final String name) {
        String encodedSessionId = sessionIdToBase64Encode(sessionId);
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        T deserializedObject = null;

        if (jedisCluster.exists(encodedSessionId)) {
            String serializedObjectString = jedisCluster.hget(encodedSessionId, name);
            if (serializedObjectString != null) {
                if (REDIS_CREATION_TIME.equals(name)) {
                    deserializedObject = (T) serializedObjectString;
                } else {
                    deserializedObject = deserialize(serializedObjectString);
                }
            }
        }
        
        return deserializedObject;
    }

    /**
     * @Description 세션 이름속성으로 값 저장하기
     * @param sessionId
     * @param name
     * @param value
     * @param <T>
     */
    public static <T extends Serializable> void setAttribute(final String sessionId, final String name, final T value) {
        try {
            String encodedSessionId = sessionIdToBase64Encode(sessionId);
            JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
            if (REDIS_CREATION_TIME.equals(name)) {
                jedisCluster.hset(encodedSessionId, name, (String) value);
            } else {
                jedisCluster.hset(encodedSessionId, name, serialize(value));
            }
            jedisCluster.expire(encodedSessionId, DEFAULT_SESSION_INTERVAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description 세션 이름속성으로 값 제거하기
     * @param sessionId
     * @param name
     */
    public static void removeAttribute(final String sessionId, final String name) {
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        String encodedSessionId = sessionIdToBase64Encode(sessionId);
        if (jedisCluster.exists(encodedSessionId)) {
            jedisCluster.hdel(encodedSessionId, name);
        }
    }

    /**
     * @Description 세션 최대 유효 시간 설정하기
     * @param sessionId
     */
    public static void setMaxInactiveInterval(final String sessionId) {
        String encodedSessionId = sessionIdToBase64Encode(sessionId);
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        if (jedisCluster.exists(encodedSessionId)) {
            jedisCluster.expire(encodedSessionId, DEFAULT_SESSION_INTERVAL);
        }
    }

    /**
     * @Description 세션 최대 유효 시간 설정하기
     * @param sessionId
     * @param interval
     */
    public static void setMaxInactiveInterval(final String sessionId, int interval) {
        String encodedSessionId = sessionIdToBase64Encode(sessionId);
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        if (jedisCluster.exists(encodedSessionId)) {
            jedisCluster.expire(encodedSessionId, interval);
        }
    }

    /**
     * @Description 세션 유효시간 조회
     * @param sessionId
     * @return
     */
    public static Long getRemainSessionExpirationTime(String sessionId) {
        String encodedSessionId = sessionIdToBase64Encode(sessionId);
        Long remainSessionExpirationTime = 0L;
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();

        if (jedisCluster.exists(encodedSessionId)) {
            remainSessionExpirationTime =  jedisCluster.ttl(encodedSessionId);
        }
        
        return remainSessionExpirationTime;
    }

    /**
     * @Description 세션 삭제하기
     * @param sessionId
     */
    public static void delete(final String sessionId) {
        String encodedSessionId = sessionIdToBase64Encode(sessionId);
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();

        if (jedisCluster.exists(encodedSessionId)) {
            jedisCluster.del(encodedSessionId);
        }
    }

    /**
     * @Description 세션 id  Bas64 기준 decode
     * @param encodedSessionId
     * @return
     */
    public static String sessionIdToBase64Decode(final String encodedSessionId) {
        byte[] bytes = Base64.getDecoder().decode(encodedSessionId);
        return new String(bytes);
    }

    /**
     * @Description 세션 id  Bas64 기준 encode
     * @param sessionId
     * @return
     */
    public static String sessionIdToBase64Encode(final String sessionId) {
        return Base64.getEncoder().encodeToString(sessionId.getBytes());
    }

    /**
     * @Description 세션에 저장된 문자열을 객체로 역직렬화
     * @param serializedObjectString
     * @param <T>
     * @return
     * @throws Exception
     */
    private static <T extends Serializable> T deserialize(String serializedObjectString) {
        byte[] bytes = Base64.getDecoder().decode(serializedObjectString.getBytes());

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (T) inputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description 세션에 저장할 객체 문자열로 직렬화
     * @param value
     * @param <T>
     * @return
     * @throws IOException
     */
    private static <T extends Serializable> String serialize(T value) {

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(value);
            outputStream.flush();
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
