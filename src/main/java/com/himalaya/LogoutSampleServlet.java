package com.himalaya;

import com.himalaya.commonUtil.redis.RedisSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutSampleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        try {
            if (RedisSession.isValid(sessionId)) {
                RedisSession.delete(sessionId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.invalidate();
    }
}
