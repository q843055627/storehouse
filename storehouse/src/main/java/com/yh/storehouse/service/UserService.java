package com.yh.storehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yh.storehouse.domain.User;

/*
 * IService 默认提供了一系列的基础方法 对应实现层也要继承对应的实现类
 */
public interface UserService extends IService<User> {

    public void saveUserRoles(Integer uid,Integer[] ids);
}
