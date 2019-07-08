package com.jty.performance.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jty.performance.support.Result;
import com.jty.performance.support.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: yeting
 * @Date: 2019/5/14 20:52
 */
@Component("performanceAuthenticationFailureHandler")
public class PerformanceAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        logger.info("登录失败");
        httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        e.printStackTrace();
        if (e instanceof DisabledException) {
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(Result.failure(ResultCode.USER_ACCOUNT_FORBIDDEN)));
        } else {
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(Result.failure(ResultCode.USER_LOGIN_ERROR)));
        }
    }
}
