package com.himalaya.commonUtil.redis.test;

import com.himalaya.UserInfo;
import com.himalaya.commonUtil.redis.RedisConnectionPool;
import com.himalaya.commonUtil.redis.RedisSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.JedisCluster;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedisClientTest {

    private String sessionId;
    private static final int TOTAL_OPERATIONS = 100000;

    @Before
    public void setUp() {
        sessionId = "5AC6C0F9FA49CF4286DEA0B1A29CAAF0";
    }

    @After
    public void tearDown() {
    }

    @Test
    public void isValidSessionTest() throws Exception {
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        jedisCluster.hset(RedisSession.sessionIdToBase64Encode(sessionId), "testName", "testValue");
        assertTrue(RedisSession.isValid(sessionId));
    }

    @Test
    public void setSessionAttributeTest() {
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        RedisSession.setAttribute(sessionId, "username", new UserInfo("sunghee", 30, "captain"));
        RedisSession.setAttribute(sessionId, "testname", "testvalue");
        RedisSession.setAttribute(sessionId, "test", "테스트");

        String encodedSessionId = RedisSession.sessionIdToBase64Encode(sessionId);
//        assertTrue(jedis.hexists(encodedSessionId, "username"));
        assertTrue(jedisCluster.hexists(encodedSessionId, "testname"));
    }

    @Test
    public void setCreationTimeTest() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        RedisSession.setAttribute(sessionId, "creationTime", strDate);

        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        String encodedSessionId = RedisSession.sessionIdToBase64Encode(sessionId);
        assertTrue(jedisCluster.hexists(encodedSessionId, "creationTime"));
    }

    @Test
    public void getCreationTimeTest() {
        String creationTime = RedisSession.getAttribute(sessionId, "creationTime");
        System.out.println("creationTime = " + creationTime);
    }

    @Test
    public void getSessionAttributeTest() {
        RedisSession.setAttribute(sessionId, "username", new UserInfo("sunghee", 30, "captain"));
        RedisSession.setAttribute(sessionId, "testname", "testvalue");

        UserInfo userInfo = (UserInfo) RedisSession.getAttribute(sessionId, "username");
        String testname = (String) RedisSession.getAttribute(sessionId, "testname");
        assertEquals("sunghee", userInfo.getName());
        assertEquals(30, userInfo.getAge());
        assertEquals("captain", userInfo.getNickName());
        assertEquals("testvalue", testname);
    }

    @Test
    public void removeSessionAttributeTest() {
        RedisSession.removeAttribute(sessionId, "username");
        String encodedSessionId = RedisSession.sessionIdToBase64Encode(sessionId);
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        assertEquals(false, jedisCluster.hexists(encodedSessionId, "username"));
    }

    @Test
    public void setSessionMaxInactiveIntervalTest() {
        RedisSession.setMaxInactiveInterval(sessionId, 30);
        String encodedSessionId = RedisSession.sessionIdToBase64Encode(sessionId);
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        assertEquals("30", jedisCluster.ttl(encodedSessionId).toString());
    }

    @Test
    public void deleteSessionTest() {
        RedisSession.delete(sessionId);
        JedisCluster jedisCluster = RedisConnectionPool.getJedisCluster();
        assertEquals(false, jedisCluster.exists(sessionId));
    }

    @Test
    public void sessionIdToBase64EncodeTest() {
        assertEquals("eW40cDdhNGJjODZ2ZjZybjU3M3pleDVv", RedisSession.sessionIdToBase64Encode(sessionId));
    }

    @Test
    public void sessionIdToBase64DecodeTest() {
        assertEquals("5AC6C0F9FA49CF4286DEA0B1A29CAAF0", RedisSession.sessionIdToBase64Decode("NUFDNkMwRjlGQTQ5Q0Y0Mjg2REVBMEIxQTI5Q0FBRjA="));
    }
    
    @Test
    public void currentDateTimeToStringTest() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        System.out.println("strDate = " + strDate);
    }
}
