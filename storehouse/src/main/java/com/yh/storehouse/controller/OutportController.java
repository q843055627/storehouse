package com.yh.storehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yh.storehouse.common.DataGridView;
import com.yh.storehouse.common.ResultObj;
import com.yh.storehouse.domain.Goods;
import com.yh.storehouse.domain.Outport;
import com.yh.storehouse.domain.Provider;
import com.yh.storehouse.service.GoodsService;
import com.yh.storehouse.service.OutportService;
import com.yh.storehouse.service.ProviderService;
import com.yh.storehouse.vo.OutportVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("outport")
public class OutportController {


    @Resource
    private OutportService outportService;
    @Resource
    private ProviderService providerService;
    @Resource
    private GoodsService goodsService;

    /**
     * 添加退货信息
     */
    @RequestMapping(value = "/addOutport",method = RequestMethod.POST)
    public ResultObj addOutport(Integer id,Integer number,String remark){
        try{
            outportService.addOutPort(id,number,remark);
            return ResultObj.RETURN_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.RETURN_FILE;
        }
    }

    @RequestMapping(value = "/loadAllOutport" , method = RequestMethod.GET)
    public DataGridView loadAllOutport(OutportVo outportVo){
        IPage<Outport> page = new Page<>(outportVo.getPage(),outportVo.getLimit());
        QueryWrapper<Outport> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(null != outportVo.getProviderid() && outportVo.getProviderid() != 0,
                "providerid",outportVo.getProviderid());
        queryWrapper.eq(null != outportVo.getGoodsid() && outportVo.getGoodsid() != 0,
                "goodsid",outportVo.getGoodsid());
        queryWrapper.ge(null != outportVo.getStartTime(),"outputtime",outportVo.getStartTime());
        queryWrapper.le(null != outportVo.getEndTime(),"outputtime",outportVo.getEndTime());
        queryWrapper.like(StringUtils.isNotBlank(outportVo.getOperateperson()), "operateperson", outportVo.getOperateperson());
        queryWrapper.like(StringUtils.isNotBlank(outportVo.getRemark()), "remark", outportVo.getRemark());
        queryWrapper.orderByDesc("outputtime");
        outportService.page(page,queryWrapper);
        List<Outport> list = page.getRecords();
        for(Outport outport : list){
            Provider provider = providerService.getById(outport.getProviderid());
            if(null != provider){
                outport.setProvidername(provider.getProvidername());
            }
            Goods goods = goodsService.getById(outport.getGoodsid());
            if(null != goods){
                outport.setGoodsname(goods.getGoodsname());
                outport.setSize(goods.getSize());
            }
        }
        return new DataGridView(page.getTotal(),list);
    }

}
