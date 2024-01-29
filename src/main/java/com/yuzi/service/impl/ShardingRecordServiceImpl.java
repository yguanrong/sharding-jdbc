package com.yuzi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuzi.entity.ShardingRecord;
import com.yuzi.mapper.ShardingRecordMapper;
import com.yuzi.service.IShardingRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 分片记录表 服务实现类
 * </p>
 *
 * @author yuzi
 * @since 2023-12-07
 */
@Service
public class ShardingRecordServiceImpl extends ServiceImpl<ShardingRecordMapper, ShardingRecord> implements IShardingRecordService {

    @Autowired
    private ShardingRecordMapper shardingRecordMapper;

    @Override
    public List<ShardingRecord> queryAllShardingRecordByLogicName(String logicTableName) {
        return shardingRecordMapper.selectListByLogicTable(logicTableName);
    }

    @Override
    public void insert(ShardingRecord shardingRecord) {
        shardingRecordMapper.insert(shardingRecord);
    }

    @Override
    public List<ShardingRecord> getList() {
        return list();
    }
}
