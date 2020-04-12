package com.yh.storehouse.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.storehouse.domain.Loginfo;
import com.yh.storehouse.mapper.LoginfoMapper;
import com.yh.storehouse.service.LoginfoService;
import org.springframework.stereotype.Service;

@Service
public class LoginfoServiceImpl extends ServiceImpl<LoginfoMapper, Loginfo> implements LoginfoService {
}
