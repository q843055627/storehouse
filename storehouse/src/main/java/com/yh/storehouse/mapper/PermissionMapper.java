package com.yh.storehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yh.storehouse.domain.Permission;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;


/*
 * 在使用Mybatis-Plus中我们可能用到一个比较多的类是BaseMapper接口,
 * BaseMapper接口默认提供了一系列的增删改查的基础方法
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    void deleteRolePermissionByPid(@Param("id")Serializable id);        //@Param 和 xml里面传递的变量要一致
}
