package com.yh.storehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yh.storehouse.domain.Role;

import java.io.Serializable;
import java.util.List;

public interface RoleService extends IService<Role> {

    public List<Integer> getPermissionByRid(Serializable id);

    public void savePermission(Integer rid , Integer[] ids);

    public List<Integer> queryRidByUid(Integer uid);
}
