package com.yh.storehouse.vo;

import com.yh.storehouse.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserVo extends User {

    private static final long serialVersionUID = 1L;

    private Integer page = 1;

    private Integer limit = 10;
}
