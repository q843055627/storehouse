package com.yh.storehouse.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
@TableName("sys_permission")
public class Permission implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private Integer pid;

    /**
     * 权限类型[menu/permission]
     */
    private String type;

    private String title;

    /**
     * 权限编码[只有type= permission才有  user:view]
     */
    private String percode;

    private String icon;

    private String href;

    private String target;

    private Integer open;

    private Integer ordernum;

    /**
     * 状态【0不可用1可用】
     */
    private Integer available;
}
