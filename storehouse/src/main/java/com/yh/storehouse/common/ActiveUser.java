package com.yh.storehouse.common;

import com.yh.storehouse.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
    @AllArgsConstructor : 使用后添加一个构造函数，该构造函数含有所有已声明字段属性参数
    @NoArgsConstructor : 使用后创建一个无参构造函数

    注：首次使用@Data时，别处可能无法引用setter和getter方法，去setting的plugins安装lombok插件即可
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveUser {

    private User user;      //用户

    private List<String> roles;         //角色

    private List<String> permissions;       //权限
}
