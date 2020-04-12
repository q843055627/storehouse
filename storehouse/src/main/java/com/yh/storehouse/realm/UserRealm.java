package com.yh.storehouse.realm;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yh.storehouse.common.ActiveUser;
import com.yh.storehouse.common.Constant;
import com.yh.storehouse.domain.Permission;
import com.yh.storehouse.domain.User;
import com.yh.storehouse.service.PermissionService;
import com.yh.storehouse.service.RoleService;
import com.yh.storehouse.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * shiro的Realm 是什么？ 其实就是个中介。它才是真正进行用户认证和授权的关键地方。
 * AuthorizingRealm  用于授权和认证的
 */

public class UserRealm extends AuthorizingRealm {

    @Autowired
    @Lazy  //不会启动时候就加载，只有在用的时候才会加载。用为////user做了缓存是动态代理(在启动之后才初始化缓存)，所以其它用到UserService的地方要在缓存之后加载
    private UserService userService;

    @Resource
    @Lazy
    private RoleService roleService;

    @Resource
    @Lazy
    private PermissionService permissionService;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        ActiveUser activeUser = (ActiveUser) principalCollection.getPrimaryPrincipal();
        User user = activeUser.getUser();       //获取用户
        List<String> permissions = activeUser.getPermissions();  //获取权限
        if(user.getType() == Constant.USER_TYPE_SUPER){  //超级管理员
            authorizationInfo.addStringPermission("*:*");  //所有权限
        }
        else{
            if(null != permissions && permissions.size() > 0){
                authorizationInfo.addStringPermissions(permissions);
            }
        }
        return authorizationInfo;
    }

    /**
     * 认证
     * @param token
     * @return
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //QueryWrapper，封装sql对象，包括where条件，order by排序，select哪些字段等等
        System.out.println("------>开始认证<------");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //根据loginname字段判断  判断对应账户是否存在
        queryWrapper.eq("loginname",token.getPrincipal().toString());
        System.out.println("------>token.getPrincipal() ： " + token.getPrincipal() +"<------");
        User user = userService.getOne(queryWrapper);
        if(null != user){
            ActiveUser activeUser = new ActiveUser();
            activeUser.setUser(user);
            //获取角色id
            Integer userId = user.getId();
            List<Integer> rids = roleService.queryRidByUid(userId);
            //获取菜单id
            Set<Integer> pidSet = new HashSet<>();
            for(Integer rid : rids){
                List<Integer> pids = roleService.getPermissionByRid(rid);
                pidSet.addAll(pids);
            }
            //根据菜单id获取对应的按钮
            List<Permission> permList = new ArrayList<>();
            QueryWrapper<Permission> qw = new QueryWrapper<>();
            qw.eq("type", Constant.TYPE_PERMISSION);
            qw.eq("available",Constant.AVAILABLE_TRUE);
            if(pidSet.size() > 0){
                qw.in("id",pidSet);
                permList = permissionService.list(qw);
            }
            //获取权限字段
            List<String> percodes = new ArrayList<>();
            if (permList.size() > 0){
                for(Permission pers : permList){
                    percodes.add(pers.getPercode());
                }
            }
            //权限放到ActiveUser中
            activeUser.setPermissions(percodes);

            ByteSource credentialsSalt = ByteSource.Util.bytes(user.getSalt());
            //userInfo, 此处传的是用户对象
            //userInfo.getPassword(),  密码—从数据库中获取的密码
            //salt,盐–用于加密密码对比
            //getName() //当前的realm名
            SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
                activeUser,user.getPwd(),credentialsSalt,this.getName()
            );
            System.out.println("------>info:"+info+"<------");
            return info;
        }
        return null;
    }
}
