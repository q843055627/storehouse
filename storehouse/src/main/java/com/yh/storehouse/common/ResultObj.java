package com.yh.storehouse.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj {

    public static final ResultObj LOGIN_SUCCESS = new ResultObj(Constant.OK,"登陆成功");
    public static final ResultObj LOGIN_ERROR_PASS = new ResultObj(Constant.ERROR,"登陆失败，账号或密码错误");
    public static final ResultObj LOGIN_ERROR_CODE = new ResultObj(Constant.ERROR,"登陆失败，验证码错误");

    public static final ResultObj DELETE_SUCCESS = new ResultObj(Constant.OK,"删除成功");
    public static final ResultObj DELETE_FILE = new ResultObj(Constant.ERROR,"删除失败");

    public static final ResultObj ADD_SUCCESS = new ResultObj(Constant.OK,"新增成功");
    public static final ResultObj ADD_FILE = new ResultObj(Constant.ERROR,"新增失败");

    public static final ResultObj UPDATE_SUCCESS = new ResultObj(Constant.OK,"修改成功");
    public static final ResultObj UPDATE_FILE = new ResultObj(Constant.ERROR,"修改失败");

    public static final ResultObj RESET_SUCCESS = new ResultObj(Constant.OK,"重置成功");
    public static final ResultObj RESET_FILE = new ResultObj(Constant.ERROR,"重置失败");

    public static final ResultObj DISPATCH_SUCCESS = new ResultObj(Constant.OK,"分配成功");
    public static final ResultObj DISPATCH_FILE = new ResultObj(Constant.ERROR,"分配失败");

    public static final ResultObj RETURN_SUCCESS = new ResultObj(Constant.OK,"退货成功");
    public static final ResultObj RETURN_FILE = new ResultObj(Constant.ERROR,"退货失败");

    private Integer code;
    private String msg;
}
