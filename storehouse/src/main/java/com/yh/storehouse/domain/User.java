package com.yh.storehouse.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

/**
 * 1.@Data：会为类的所有属性自动生成setter/getter、equals、canEqual、hashCode、toString方法，
 * 如为final属性，则不会为该属性生成setter方法
 * 2.@EqualsAndHashCode: 在编译成class文件时，自动给类重写equals()、hashCode()方法,
 * 它默认仅使用该类中定义的属性且不调用父类的方法  (callSuper = false)
 * 3.使用@Data时同时加上@EqualsAndHashCode(callSuper=false)注解
 * 4.@Accessors  翻译是存取器。通过该注解可以控制getter和setter方法的形式,chain 若为true，则setter方法返回当前对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class User implements Serializable {  //Serializable接口可序列化或反序列化

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type= IdType.AUTO)    //@TableId：表主键标识,IdType.AUTO是自增
    private Integer id;

    private String name;

    private String loginname;

    private String address;

    private Integer sex;

    private String remark;

    private String pwd;

    private Integer deptid;

    private Date hiredate;

    private Integer mgr;

    private Integer available;

    private Integer ordernum;

    /**
     * 用户类型[0超级管理员1，管理员，2普通用户]
     */
    private Integer type;

    /**
     * 头像地址
     */
    private String imgpath;

    private String salt;

    @TableField(exist = false)      //表示当前属性不是数据表的字段，但在项目中使用
    private String leadername;

    @TableField(exist = false)
    private String deptname;
}
