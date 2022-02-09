package com.himalaya;

import com.himalaya.commonUtil.redis.RedisSession;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException {
        System.out.println("HelloServlet.doGet");
        HttpSession session = req.getSession();
        String sessionId = session.getId();
        System.out.println("before sessionId = " + sessionId);

        try {
            if (!RedisSession.isValid(sessionId)) {
                session.invalidate();
                sessionId = req.getSession(true).getId();
                System.out.println("after sessionId = " + sessionId);
                session.setMaxInactiveInterval(-1);
                Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                RedisSession.setAttribute(sessionId, "creationTime", dateFormat.format(date));
            } else {
                RedisSession.setMaxInactiveInterval(sessionId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        response.getWriter().println("hello");
    }
}
