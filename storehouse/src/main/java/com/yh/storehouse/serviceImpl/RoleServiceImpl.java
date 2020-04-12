package com.yh.storehouse.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.storehouse.domain.Role;
import com.yh.storehouse.mapper.RoleMapper;
import com.yh.storehouse.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Override
    public boolean removeById(Serializable id) {
        roleMapper.deleteRolePermissionByRid(id);
        getBaseMapper().deleteRoleUserByRid(id);            // getBaseMapper() ==   roleMapper
        return super.removeById(id);        //删除角色表
    }

    @Override
    public List<Integer> getPermissionByRid(Serializable id) {
        return roleMapper.getPermissionByRid(id);
    }

    @Override
    public void savePermission(Integer rid, Integer[] ids) {
        //先删除后保存
        getBaseMapper().deleteRolePermissionByRid(rid);     //删除对应的所有权限
        if(null != ids && ids.length > 0){      //如果是没有选择任何权限，没必要走保存
            for(Integer id : ids){
                getBaseMapper().addRolePermission(rid,id);
            }
        }
    }

    @Override
    public List<Integer> queryRidByUid(Integer uid) {
        return roleMapper.queryRidByUid(uid);
    }
}
