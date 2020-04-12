package com.yh.storehouse.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yh.storehouse.common.DataGridView;
import com.yh.storehouse.common.ResultObj;
import com.yh.storehouse.common.WebUtils;
import com.yh.storehouse.domain.Notice;
import com.yh.storehouse.domain.User;
import com.yh.storehouse.service.NoticeService;
import com.yh.storehouse.vo.NoticeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @RequestMapping("/loadAllNotice")
    private DataGridView loadAllNotice(NoticeVo noticeVo){
        IPage<Notice> page = new Page<>(noticeVo.getPage(),noticeVo.getLimit());
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(noticeVo.getTitle()),"title",noticeVo.getTitle());
        queryWrapper.like(StringUtils.isNotBlank(noticeVo.getOpername()),"opername",noticeVo.getOpername());
        queryWrapper.ge(noticeVo.getStartTime() != null ,"createtime", noticeVo.getStartTime());
        queryWrapper.le(noticeVo.getEndTime()!=null, "createtime", noticeVo.getEndTime());
        queryWrapper.orderByDesc("createtime");
        noticeService.page(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @RequestMapping("/deleteNotice")
    public ResultObj deleteNotice(Integer id){
        try{
            noticeService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_FILE;
        }
    }

    @RequestMapping("/batchDeleteNotice")
    public ResultObj batchDeleteNotice(NoticeVo noticeVo){
        try{
            List<Integer> idList = Arrays.asList(noticeVo.getIds());
            noticeService.removeByIds(idList);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_FILE;
        }
    }

    @RequestMapping("/addNotice")
    public ResultObj addNotice(NoticeVo noticeVo){
        try{
            User user = (User) WebUtils.getSession().getAttribute("user");
            Notice notice = new Notice();
            notice.setTitle(noticeVo.getTitle());
            notice.setContent(noticeVo.getContent());
            notice.setCreatetime(new Date());
            notice.setOpername(user.getName());
            noticeService.save(notice);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e){
            e.printStackTrace();
            return ResultObj.ADD_FILE;
        }
    }

    @RequestMapping("/updateNotice")
    public ResultObj updateNotice(NoticeVo noticeVo){
        try{
            noticeService.updateById(noticeVo);
            return ResultObj.UPDATE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.UPDATE_FILE;
        }
    }
}
