package com.yh.storehouse.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yh.storehouse.domain.Notice;
import com.yh.storehouse.mapper.NoticeMapper;
import com.yh.storehouse.service.NoticeService;
import org.springframework.stereotype.Service;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
}
