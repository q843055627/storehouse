package com.yh.storehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yh.storehouse.common.DataGridView;
import com.yh.storehouse.common.ResultObj;
import com.yh.storehouse.common.TreeNode;
import com.yh.storehouse.domain.Dept;
import com.yh.storehouse.service.DeptService;
import com.yh.storehouse.vo.DeptVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("dept")
public class DeptController {

    @Resource
    private DeptService deptService;

    @RequestMapping("/loadDeptManagerLeftTreeJson")
    private DataGridView loadDeptManagerLeftTreeJson(DeptVo deptVo){
        List<Dept> list = new ArrayList<>();
        List<TreeNode> treeNodes = new ArrayList<>();

        list  = deptService.list();     //查询所有
        for(Dept dept : list){
            Boolean spread = dept.getOpen() == 1?true:false;
            treeNodes.add(new TreeNode(dept.getId(),dept.getPid(),dept.getTitle(),spread));
        }
        return new DataGridView(treeNodes);
    }

    @RequestMapping("/loadAllDept")
    public DataGridView loadAllDept(DeptVo deptVo){
        IPage<Dept> page = new Page<>(deptVo.getPage(),deptVo.getLimit());
        QueryWrapper<Dept> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(deptVo.getId() != null , "id" , deptVo.getId())
                .or().eq(deptVo.getId() != null,"pid" , deptVo.getId());
        queryWrapper.like(StringUtils.isNotBlank(deptVo.getTitle()), "title", deptVo.getTitle());
        queryWrapper.like(StringUtils.isNotBlank(deptVo.getAddress()), "address", deptVo.getAddress());
        queryWrapper.like(StringUtils.isNotBlank(deptVo.getRemark()), "remark", deptVo.getRemark());

        queryWrapper.orderByAsc("ordernum");
        deptService.page(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @RequestMapping("/addDept")
    public ResultObj addDept(DeptVo deptVo){
        try{        //新增缺少时间
            deptVo.setCreatetime(new Date());
            deptService.save(deptVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e){
            e.printStackTrace();
            return ResultObj.ADD_FILE;
        }
    }

    @RequestMapping("/updateDept")
    public ResultObj updateDept(DeptVo deptVo){
        try{
            deptService.updateById(deptVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e){
            e.printStackTrace();
            return ResultObj.UPDATE_FILE;
        }
    }

    @RequestMapping("/deleteDept")
    public ResultObj deleteDept(DeptVo deptVo){
        try{
           deptService.removeById(deptVo.getId());
           return ResultObj.DELETE_SUCCESS;
        } catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_FILE;
        }
    }

    /*
    *  判断是否有子菜单
    */
    @RequestMapping("/isHasChildDept")
    public Map<String , Object> isHasChildDept(DeptVo deptVo){
        Map<String , Object> map = new HashMap<>();
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid",deptVo.getId());      //子部门匹配
        List<Dept> list = deptService.list(queryWrapper);
        if(list.size() > 0){        //有子部门
            map.put("value",true);
        }
        else {
            map.put("value",false);
        }
        return map;
    }

    /*
     *  获取最大排序码
     */
    @RequestMapping("/getMaxOrderNum")
    public Map<String , Object> getMaxOrderNum(){
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("ordernum");
        List<Dept> list = deptService.list(queryWrapper);
        Map<String , Object> map = new HashMap<>();
        if(list.size() > 0){
            map.put("value",list.get(0).getOrdernum() + 1);
        }
        else {
            map.put("value",1);
        }
        return map;
    }
}
