package com.yh.storehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yh.storehouse.domain.User;

/*
* 在使用Mybatis-Plus中我们可能用到一个比较多的类是BaseMapper接口,
* BaseMapper接口默认提供了一系列的增删改查的基础方法
 */

public interface UserMapper extends BaseMapper<User> {
}
