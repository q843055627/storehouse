package com.yh.storehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yh.storehouse.common.*;
import com.yh.storehouse.domain.Permission;
import com.yh.storehouse.domain.User;
import com.yh.storehouse.service.PermissionService;
import com.yh.storehouse.service.RoleService;
import com.yh.storehouse.vo.PermissionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("menu")
public class MenuController {

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleService roleService;

    /*
    * 登录成功加载左边的菜单栏
    * */
    @RequestMapping(value = "/loadMenuList")
    public DataGridView loadMenuList(PermissionVo permissionVo){
        //查询所有  mybatis-plus------QueryWrapper(条件构造类)
        QueryWrapper<Permission> queryWrapper = new QueryWrapper<>();
        //设置查询条件---获取可得的menu菜单
        queryWrapper.eq("type", Constant.TYPE_MNEU);
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);

        List<Permission> menuList = null;
        User user = (User) WebUtils.getSession().getAttribute("user");

        if(user.getType() == Constant.USER_TYPE_SUPER){
            menuList = permissionService.list(queryWrapper);
        }
        else {
            //根据用户角色 + 权限查询
            int uid = user.getId();
            //根据uid获取rid
            List<Integer> rids = roleService.queryRidByUid(uid);
            Set<Integer> pidSet = new HashSet<>();      //set去重
            //根据rid获取菜单
            for(Integer rid : rids){
                List<Integer> pids = roleService.getPermissionByRid(rid);
                pidSet.addAll(pids);
            }
            //根据rid获取菜单
            if(pidSet.size()>0){
                queryWrapper.in("id",pidSet);
                menuList = permissionService.list(queryWrapper);
            }else{
                menuList = new ArrayList<>();
            }
        }
        //对查询到的菜单进行处理
        List<TreeNode> treeNodes = new ArrayList<>();
        for (Permission menu : menuList){
            Integer id=menu.getId();
            Integer pid=menu.getPid();
            String title=menu.getTitle();
            String icon=menu.getIcon();
            String href=menu.getHref();
            Boolean spread=menu.getOpen()==Constant.OPEN_TRUE?true:false;
            //构建 TreeNode 参数
            treeNodes.add(new TreeNode(id, pid, title, icon, href, spread));
        }
        List<TreeNode> list = TreeNodeBuilder.build(treeNodes,1);
        return new DataGridView(list);
    }

    /****************菜单管理开始****************/

    /**
     * 加载菜单管理左边的菜单树的json
     */
    @RequestMapping("loadMenuManagerLeftTreeJson")
    public DataGridView loadMenuManagerLeftTreeJson(PermissionVo permissionVo) {
        QueryWrapper<Permission> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("type", Constant.TYPE_MNEU);        //只加载菜单
        List<Permission> list = this.permissionService.list(queryWrapper);
        List<TreeNode> treeNodes=new ArrayList<>();
        for (Permission menu : list) {
            Boolean spread=menu.getOpen()==1?true:false;
            treeNodes.add(new TreeNode(menu.getId(), menu.getPid(), menu.getTitle(), spread));
        }
        return new DataGridView(treeNodes);
    }

    /**
     * 查询
     */
    @RequestMapping("loadAllMenu")
    public DataGridView loadAllMenu(PermissionVo permissionVo) {
        IPage<Permission> page=new Page<>(permissionVo.getPage(), permissionVo.getLimit());
        QueryWrapper<Permission> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq(permissionVo.getId()!=null, "id", permissionVo.getId()).or().eq(permissionVo.getId()!=null,"pid", permissionVo.getId());
        queryWrapper.eq("type", Constant.TYPE_MNEU);//只能查询菜单
        queryWrapper.like(StringUtils.isNotBlank(permissionVo.getTitle()), "title", permissionVo.getTitle());
        queryWrapper.orderByAsc("ordernum");
        this.permissionService.page(page, queryWrapper);
        return new DataGridView(page.getTotal(), page.getRecords());
    }

    /**
     * 加载最大的排序码
     * @param permissionVo
     * @return
     */
    @RequestMapping("loadMenuMaxOrderNum")
    public Map<String,Object> loadMenuMaxOrderNum(){
        Map<String, Object> map=new HashMap<String, Object>();

        QueryWrapper<Permission> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByDesc("ordernum");
        IPage<Permission> page=new Page<>(1, 1);
        List<Permission> list = this.permissionService.page(page, queryWrapper).getRecords();
        if(list.size()>0) {
            map.put("value", list.get(0).getOrdernum()+1);
        }else {
            map.put("value", 1);
        }
        return map;
    }


    /**
     * 添加
     * @param permissionVo
     * @return
     */
    @RequestMapping("addMenu")
    public ResultObj addMenu(PermissionVo permissionVo) {
        try {
            permissionVo.setType(Constant.TYPE_MNEU);//设置添加类型
            this.permissionService.save(permissionVo);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_FILE;
        }
    }


    /**
     * 修改
     * @param permissionVo
     * @return
     */
    @RequestMapping("updateMenu")
    public ResultObj updateMenu(PermissionVo permissionVo) {
        try {
            this.permissionService.updateById(permissionVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_FILE;
        }
    }


    /**
     * 查询当前的ID的菜单有没有子菜单
     */
    @RequestMapping("checkMenuHasChildrenNode")
    public Map<String,Object> checkMenuHasChildrenNode(PermissionVo permissionVo){
        Map<String, Object> map=new HashMap<String, Object>();

        QueryWrapper<Permission> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("pid", permissionVo.getId());
        List<Permission> list = this.permissionService.list(queryWrapper);
        if(list.size()>0) {
            map.put("value", true);
        }else {
            map.put("value", false);
        }
        return map;
    }

    /**
     * 删除
     * @param permissionVo
     * @return
     */
    @RequestMapping("deleteMenu")
    public ResultObj deleteMenu(PermissionVo permissionVo) {
        try {
            //还要删除对应的权限表和角色表信息
            this.permissionService.removeById(permissionVo.getId());
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_FILE;
        }
    }

    /****************菜单管理结束****************/
}
