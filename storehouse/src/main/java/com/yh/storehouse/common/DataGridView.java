package com.yh.storehouse.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataGridView {
    /*
    *   返回指定格式数据到前端
    * */
    private Integer code = 0;
    private String msg = "";
    private Long count = 0L;
    private Object data;

    public DataGridView(Long count,Object data){
        super();
        this.count = count;
        this.data = data;
    }

    public DataGridView(Object data){
        super();
        this.data = data;
    }
}
