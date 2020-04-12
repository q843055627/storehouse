package com.yh.storehouse.controller;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yh.storehouse.common.Constant;
import com.yh.storehouse.common.DataGridView;
import com.yh.storehouse.common.PinyinUtils;
import com.yh.storehouse.common.ResultObj;
import com.yh.storehouse.domain.Dept;
import com.yh.storehouse.domain.Role;
import com.yh.storehouse.domain.User;
import com.yh.storehouse.service.DeptService;
import com.yh.storehouse.service.RoleService;
import com.yh.storehouse.service.UserService;
import com.yh.storehouse.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private DeptService deptService;

    @Resource
    private RoleService roleService;


    //不指定method，则任何请求形式都可以访问
    @RequestMapping("/loadAllUser")
    public DataGridView loadAllUser(UserVo userVo){
        IPage<User> page = new Page<>(userVo.getPage(),userVo.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(userVo.getName()),"loginname",userVo.getName())
                .or().eq(StringUtils.isNotBlank(userVo.getName()), "name", userVo.getName());
        queryWrapper.eq(StringUtils.isNotBlank(userVo.getAddress()),"address",userVo.getAddress());
        queryWrapper.eq("type", Constant.USER_TYPE_NORMAL);        //系统用户
        queryWrapper.eq(userVo.getDeptid() != null,"deptid",userVo.getDeptid());
        userService.page(page,queryWrapper);

        List<User> userList = page.getRecords();
        for(User user : userList){
            //获取对应的部门信息
            Integer deptId = user.getDeptid();
            if(null != deptId){
                Dept dept = deptService.getById(deptId);    //aop切入缓存处理
                user.setDeptname(dept.getTitle());
            }
            //获取对应的领导信息
            Integer mgrId = user.getMgr();

            if(null != mgrId){
                User one = userService.getById(mgrId);      //领导也是用户
                user.setLeadername(one.getName());
            }
        }
        return new DataGridView(page.getTotal(),userList);
    }
    /*
    * 根据id获取可用的用户信息
    * */
    @RequestMapping(value = "/loadUsersByDeptId")
    public DataGridView loadUsersByDeptId(Integer id){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null,"deptid",id);
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        queryWrapper.eq("type",Constant.USER_TYPE_NORMAL);

        List<User> list = userService.list(queryWrapper);
        return new DataGridView(list);
    }

    @RequestMapping("/loadUserById")
    public DataGridView loadUserById(Integer leaderId){
        if(null != leaderId){
            User user = userService.getById(leaderId);
            return new DataGridView(user);
        }
        else
            return null;
    }

    /**
     * 加载最大的排序码
     * @param
     * @return
     */
    @RequestMapping("/loadUserMaxOrderNum")
    public Map<String , Object> loadUserMaxOrderNum(){
        Map<String , Object> map = new HashMap<>();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("ordernum");

        List<User> userList = userService.list(queryWrapper);

        if(userList.size() > 0){
            map.put("value",userList.get(0).getOrdernum() + 1);
        }
        else {
            map.put("value",1);
        }
        return map;
    }

    @RequestMapping("/changeChineseToPinyin")
    public Map<String,Object> changeChineseToPinyin(String username){
        Map<String,Object> map = new HashMap<>();
        if(null != username && !username.equals("")){
            map.put("value", PinyinUtils.getPingYin(username));
        }
        else{
            map.put("value","");
        }
        return map;
    }

    @RequestMapping("/addUser")
    public ResultObj addUser(UserVo userVo){
       try{
           Integer mgr = userVo.getMgr();
           if(null!= mgr && mgr == 0){      //可能选了领导部门，但没选领导。则默认没选
               userVo.setMgr(null);
           }
           userVo.setType(Constant.USER_TYPE_NORMAL);   //类型
           userVo.setHiredate(new Date());
           String salt = IdUtil.simpleUUID().toUpperCase();     //生成盐
           userVo.setSalt(salt);
           //设置加密后的密码
           userVo.setPwd(new Md5Hash(Constant.USER_DEFAULT_PWD,salt,2).toString());
           userService.save(userVo);
           return ResultObj.ADD_SUCCESS;
       }
       catch (Exception e){
           e.printStackTrace();
       }return ResultObj.ADD_FILE;
    }

    @RequestMapping("/updateUser")
    public ResultObj updateUser(UserVo userVo){
        //因为修改和添加面板是一样的，所以直接保存就可
        try{
            userService.updateById(userVo);
            return ResultObj.UPDATE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.UPDATE_FILE;
        }
    }

    @RequestMapping("/deleteUser")
    public ResultObj deleteUser(Integer id){
        try{
            userService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_FILE;
        }
    }

    @RequestMapping("/resetPwd")
    public ResultObj resetPwd(Integer id){
        try{
            User user = new User();
            user.setId(id);
            String salt = IdUtil.simpleUUID().toLowerCase();
            user.setSalt(salt);
            user.setPwd(new Md5Hash(Constant.USER_DEFAULT_PWD,salt,2).toString());
            userService.updateById(user);
            return ResultObj.RESET_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.RESET_FILE;
        }
    }

    /*
    *  查询用户权限
    * */
    @RequestMapping("/initRoleByUserId")
    public DataGridView initRoleByUserId(Integer id){
        //1。查询所有可用的角色
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        List<Map<String, Object>> listMaps = roleService.listMaps(queryWrapper);
        //2.拆线呢当前用户拥有的角色
        List<Integer> currentRoles = roleService.queryRidByUid(id);
        //3.处理
        for(Map<String, Object> map : listMaps){
            Boolean checked = false;
            Integer roleId = (Integer) map.get("id");
            for(Integer rid : currentRoles){
                if(roleId == rid){
                    checked = true;
                    break;
                }
            }
            map.put("LAY_CHECKED",checked);
        }
        return new DataGridView(Long.valueOf(listMaps.size()) , listMaps);
    }

    @RequestMapping("/saveUserRole")
    public ResultObj saveUserRole(Integer uid,String ids){
        try{
            String[] idsStr = ids.split(",");
            Integer[] roleIds = new Integer[idsStr.length];
            for(int i=0;i<idsStr.length;i++){
                if(!idsStr[i].equals("")){
                    roleIds[i] = Integer.parseInt(idsStr[i]);
                }
                else{
                    roleIds[i] = -1;
                }
            }
            userService.saveUserRoles(uid,roleIds);
            return ResultObj.DISPATCH_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DISPATCH_FILE;
        }
    }
}
