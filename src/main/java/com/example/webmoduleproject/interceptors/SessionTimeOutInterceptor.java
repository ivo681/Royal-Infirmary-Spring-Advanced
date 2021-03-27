package com.example.webmoduleproject.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionTimeOutInterceptor implements HandlerInterceptor {
    private static final long MAX_INACTIVE_SESSION_TIME = 300 * 10000;
    private static final Logger log = LoggerFactory.getLogger(SessionTimeOutInterceptor.class);
    @Autowired
    private HttpSession session;

    public SessionTimeOutInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("executionTime", startTime);

        if (UserInterceptor.isUserLogged()) {
            session = request.getSession();
            log.info("Time since last request in this session: {} ms",
                    System.currentTimeMillis() - request.getSession().getLastAccessedTime());
            if (System.currentTimeMillis() - session.getLastAccessedTime()
                    > MAX_INACTIVE_SESSION_TIME) {
                log.warn("Logging out, due to inactive session");
                SecurityContextHolder.clearContext();
                request.logout();
                response.sendRedirect("/users/login-expired");
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("Post handle method - check execution time of handling");
        long startTime = (Long) request.getAttribute("executionTime");
        log.info("Execution time for handling the request was: {} ms",
                System.currentTimeMillis() - startTime);
    }
}
