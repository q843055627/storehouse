package com.yh.storehouse.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.storehouse.domain.Goods;
import com.yh.storehouse.domain.Inport;
import com.yh.storehouse.mapper.GoodsMapper;
import com.yh.storehouse.mapper.InportMapper;
import com.yh.storehouse.service.InportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.Serializable;

@Service
@Transactional
public class InportServiceImpl extends ServiceImpl<InportMapper, Inport> implements InportService {

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public boolean save(Inport entity) {
        //商品数量对应变化
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        goods.setNumber(goods.getNumber() + entity.getNumber());
        goodsMapper.updateById(goods);
        //保存进货信息
        return super.save(entity);
    }

    @Override
    public boolean updateById(Inport entity) {
        Inport inport = baseMapper.selectById(entity.getId());
        Goods goods = goodsMapper.selectById(entity.getGoodsid());
        //库存的算法  当前库存-进货单修改之前的数量+修改之后的数量
        goods.setNumber(goods.getNumber() - inport.getNumber() + entity.getNumber());
        goodsMapper.updateById(goods);
        //更新进货单
        return super.updateById(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        Inport inport = baseMapper.selectById(id);
        Goods goods = goodsMapper.selectById(inport.getGoodsid());
        //库存的算法  当前库存-进货单数量
        goods.setNumber(goods.getNumber() - inport.getNumber());
        goodsMapper.updateById(goods);
        return super.removeById(id);
    }
}
