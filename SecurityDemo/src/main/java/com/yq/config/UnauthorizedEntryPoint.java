package com.yq.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnauthorizedEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            if(isAjaxRequest(request)){
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,authException.getMessage());
            }else{
                request.getSession().invalidate();
                //response.sendRedirect(ConfigMapCache.initMap.get("unlogin").toString());
                response.sendError(450,"unlogin");
            }
        }

        public static boolean isAjaxRequest(HttpServletRequest request) {
            String ajaxFlag = request.getHeader("X-Requested-With");
            return ajaxFlag != null && "XMLHttpRequest".equals(ajaxFlag);
        }
    }