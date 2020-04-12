package com.yh.storehouse.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.storehouse.domain.Permission;
import com.yh.storehouse.mapper.PermissionMapper;
import com.yh.storehouse.service.PermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@Transactional
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper , Permission> implements PermissionService{

    @Override
    public boolean removeById(Serializable id) {

        PermissionMapper permissionMapper = this.getBaseMapper();       //拿到mapper对象  和new 的一样
        permissionMapper.deleteRolePermissionByPid(id);     //删除权限菜单表
        return super.removeById(id);        //删除菜单表
    }
}
