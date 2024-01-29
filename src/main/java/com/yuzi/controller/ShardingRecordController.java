package com.yuzi.controller;


import com.yuzi.dto.QueryParam;
import com.yuzi.entity.ShardingRecord;
import com.yuzi.entity.User;
import com.yuzi.service.IShardingRecordService;
import com.yuzi.service.IUserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 分片记录表 前端控制器
 * </p>
 *
 * @author yuzi
 * @since 2023-12-07
 */
@RestController
@RequestMapping("/shardingRecord")
public class ShardingRecordController {

    @Resource
    IShardingRecordService recordService;

    @PostMapping("/getRecordList")
    public List<ShardingRecord> getRecordList(){
        List<ShardingRecord> records = recordService.getList();
        return records;
    }
}
