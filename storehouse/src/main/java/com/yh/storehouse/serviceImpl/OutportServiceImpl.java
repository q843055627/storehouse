package com.yh.storehouse.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.storehouse.common.WebUtils;
import com.yh.storehouse.domain.Goods;
import com.yh.storehouse.domain.Inport;
import com.yh.storehouse.domain.Outport;
import com.yh.storehouse.domain.User;
import com.yh.storehouse.mapper.OutportMapper;
import com.yh.storehouse.service.GoodsService;
import com.yh.storehouse.service.InportService;
import com.yh.storehouse.service.OutportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Transactional
public class OutportServiceImpl extends ServiceImpl<OutportMapper, Outport> implements OutportService {

    @Resource
    private InportService inportService;
    @Resource
    private GoodsService goodsService;

    @Override
    public void addOutPort(Integer id, Integer number, String remark) {
        //查询进货单信息
        Inport inport = inportService.getById(id);
        Goods goods = goodsService.getById(inport.getGoodsid());
        //更新退货后的商品数量
        goods.setNumber(goods.getNumber() - number);
        goodsService.updateById(goods);
        //添加退货单信息
        Outport outport = new Outport();
        outport.setGoodsid(inport.getGoodsid());
        outport.setNumber(number);
        outport.setOutportprice(inport.getInportprice());
        outport.setOutputtime(new Date());
        outport.setPaytype(inport.getPaytype());
        outport.setProviderid(inport.getProviderid());
        outport.setRemark(remark);
        User user = (User) WebUtils.getSession().getAttribute("user");
        outport.setOperateperson(user.getName());
        getBaseMapper().insert(outport);
    }
}
