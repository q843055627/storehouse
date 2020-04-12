package com.yh.storehouse.serviceImpl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.storehouse.domain.User;
import com.yh.storehouse.mapper.RoleMapper;
import com.yh.storehouse.mapper.UserMapper;
import com.yh.storehouse.service.RoleService;
import com.yh.storehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;


@Service
@Transactional  //使用这个注解,如果失败则spring负责回滚操作，成功则提交操作
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private RoleMapper roleMapper;

    @Override
    public boolean updateById(User entity) {
        return super.updateById(entity);
    }

    @Override
    public boolean save(User entity) {
        return super.save(entity);
    }

    @Override
    public User getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean removeById(Serializable id) {
        roleMapper.deleteRoleUserByUid(id);
        //删除角色与用户关联表
        return super.removeById(id);
    }

    @Override
    public void saveUserRoles(Integer uid, Integer[] ids) {
        //先删除原来的信息
        roleMapper.deleteRoleUserByUid(uid);
        if(null != ids && ids.length>0){
            for(Integer rid : ids){
                if(rid != -1)
                    roleMapper.addUserRole(rid,uid);
            }
        }
    }
}
