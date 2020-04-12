package com.yh.storehouse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yh.storehouse.domain.Role;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {

    public void deleteRolePermissionByRid(Serializable id);

    public void deleteRoleUserByRid(Serializable id);

    public void deleteRoleUserByUid(Serializable id);

    public List<Integer> queryRidByUid(Serializable id);

    public List<Integer> getPermissionByRid(Serializable id);

    public void addUserRole(@Param("rid") Integer rid,@Param("uid") Integer uid);

    //多个参数时用@Param 把参数对应起来
    public void addRolePermission(@Param("rid") Integer rid , @Param("pid") Integer pid);
}
