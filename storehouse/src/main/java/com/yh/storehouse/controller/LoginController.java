package com.yh.storehouse.controller;

import com.yh.storehouse.common.ActiveUser;
import com.yh.storehouse.common.ResultObj;
import com.yh.storehouse.common.WebUtils;
import com.yh.storehouse.domain.Loginfo;
import com.yh.storehouse.service.LoginfoService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;


@RestController
@RequestMapping("login")
public class LoginController {

    @Resource
    private LoginfoService loginfoService;

    @RequestMapping("/login")
    public ResultObj login(String loginname , String pwd){
        Subject subject = SecurityUtils.getSubject();       //shiro包
        AuthenticationToken token = new UsernamePasswordToken(loginname,pwd);

        try{
            subject.login(token);
            ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
            WebUtils.getSession().setAttribute("user",activeUser.getUser());
            Loginfo loginfo = new Loginfo();
            loginfo.setLoginname(activeUser.getUser().getName() + "-" + activeUser.getUser().getLoginname());
            loginfo.setLoginip(WebUtils.getRequest().getRemoteAddr());      //获取id地址  ,用localhost访问可能是 0:0:0:0:0:0:0:1
            loginfo.setLogintime(new Date());
            loginfoService.save(loginfo);
            return ResultObj.LOGIN_SUCCESS;
        } catch (AuthenticationException e){
            e.printStackTrace();
            return ResultObj.LOGIN_ERROR_PASS;
        }
    }
}
