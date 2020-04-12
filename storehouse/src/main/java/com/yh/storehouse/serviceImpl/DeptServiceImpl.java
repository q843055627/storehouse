package com.yh.storehouse.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.storehouse.domain.Dept;
import com.yh.storehouse.mapper.DeptMapper;
import com.yh.storehouse.service.DeptService;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    //重写这三个方法，因本项目部门管理用到这三个方法，做缓存时用


    @Override
    public Dept getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public boolean updateById(Dept entity) {
        // TODO Auto-generated method stub
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public boolean save(Dept entity) {
        return super.save(entity);
    }
}
