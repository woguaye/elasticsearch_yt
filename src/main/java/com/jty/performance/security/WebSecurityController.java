package com.jty.performance.security;

import com.jty.performance.support.Result;
import com.jty.performance.support.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: yeting
 * @Date: 2019/5/14 22:24
 */
@RestController
public class WebSecurityController {

    @RequestMapping("/authentication/require")
    @ResponseStatus(code= HttpStatus.UNAUTHORIZED)
    public Result requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SavedRequest savedRequest = requestCache.getRequest(request, response);
////        if (savedRequest != null) {
////            String targetUrl = savedRequest.getRedirectUrl();
////            logger.info("引发跳转的请求是："+targetUrl);
////            if(StringUtils.endsWithIgnoreCase(targetUrl,".html")){
////                redirectStrategy.sendRedirect(request,response,properties.getLoginPage());
////            }
////        }
        return Result.failure(ResultCode.USER_NOT_LOGGED_IN);
    }

}
