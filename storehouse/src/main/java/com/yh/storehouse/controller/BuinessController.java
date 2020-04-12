package com.yh.storehouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("bus")
public class BuinessController {

    @RequestMapping("/toCustomerManager")
    public String toCustomerManager(){
        return "business/customer/customerManager";
    }

    @RequestMapping("/toProviderManager")
    public String toProviderManager(){
        return "business/provider/providerManager";
    }

    @RequestMapping("/toGoodsManager")
    public String toGoodsManager(){
        return "business/goods/goodsManager";
    }

    @RequestMapping("toInportManager")
    public String toInportManager() {
        return "business/inport/inportManager";
    }
    /**
     * 跳转到退货查询管理
     */
    @RequestMapping("toOutportManager")
    public String toOutportManager() {
        return "business/outport/outportManager";
    }
}
