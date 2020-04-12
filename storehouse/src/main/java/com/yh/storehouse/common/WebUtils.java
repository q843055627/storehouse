package com.yh.storehouse.common;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class WebUtils {

    /*
    * 获取request
    * */
    public static HttpServletRequest getRequest(){
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request;
    }

    /*
    * 获取session
    * */

    public static HttpSession getSession(){
        return getRequest().getSession();
    }
}
