package com.yuzi.service;

import com.yuzi.entity.ShardingRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 分片记录表 服务类
 * </p>
 *
 * @author yuzi
 * @since 2023-12-07
 */
public interface IShardingRecordService extends IService<ShardingRecord> {

    List<ShardingRecord> queryAllShardingRecordByLogicName(String logicTableName);

    void insert(ShardingRecord shardingRecord);

    List<ShardingRecord> getList();

}
