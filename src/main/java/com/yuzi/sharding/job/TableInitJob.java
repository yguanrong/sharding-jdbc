package com.yuzi.sharding.job;

import com.yuzi.config.ConfigProperty;
import com.yuzi.entity.ShardingRecord;
import com.yuzi.service.IShardingRecordService;
import com.yuzi.sharding.ShardingAlgorithmTool;
import com.yuzi.sharding.enums.ShardingTableCacheEnum;
import com.yuzi.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class TableInitJob {

    @Resource
    private ConfigProperty configProperty;

    private final String TABLE_SPLIT_SYMBOL = "_";

    @Resource
    private IShardingRecordService shardingRecordService;

    @Scheduled(cron = "${sharding.table.init.job:0 0 2 * * ?}")
    public void createTableAndDeleteExpiredTable() {
        // 初始化未来的表-3天
        ShardingTableCacheEnum.logicTableNames().forEach(logicTable->{
            List<ShardingRecord> shardingRecords = shardingRecordService.queryAllShardingRecordByLogicName(logicTable);
            if (CollectionUtils.isEmpty(shardingRecords)){
                int index = 1;

                // 从开始时间开始往后创建，直到 endTime > 当前时间 + configProperty.shardingTimeDys 就结束
                LocalDateTime startTime = DateUtil.string2LocalDateTime(configProperty.getShardingStartTime(),null);
                LocalDateTime endTime = DateUtil.getEndDateTime(DateUtil.addDays(startTime, configProperty.getShardingTimeDys()));
                while (!endTime.isAfter(DateUtil.addDays(LocalDateTime.now(),configProperty.getShardingTimeDys()))){
                    String tableName = logicTable + TABLE_SPLIT_SYMBOL + index;
                    ShardingAlgorithmTool.createShardingTable(ShardingTableCacheEnum.of(logicTable),tableName,startTime,endTime);
                    startTime = DateUtil.addDays(startTime,configProperty.getShardingTimeDys() + 1);
                    endTime = DateUtil.addDays(endTime,configProperty.getShardingTimeDys() + 1);
                    index++;
                }

            }else {
                // 从最后时间往后开始创建 直到 endTime > 当前时间 + configProperty.shardingTimeDys 就结束
                ShardingRecord shardingRecord = shardingRecords.get(shardingRecords.size() - 1);
                int index = shardingRecord.getIndexNum().intValue() + 1;

                // 从开始时间开始往后创建，直到 endTime > 当前时间 + configProperty.shardingTimeDys 就结束
                LocalDateTime startTime = shardingRecord.getStartTime();
                LocalDateTime endTime = shardingRecord.getEndTime();

                while (!endTime.isAfter(DateUtil.addDays(LocalDateTime.now(),configProperty.getShardingTimeDys()))){
                    String tableName = logicTable + TABLE_SPLIT_SYMBOL + index;
                    startTime = DateUtil.addDays(startTime,configProperty.getShardingTimeDys() + 1);
                    endTime = DateUtil.addDays(endTime,configProperty.getShardingTimeDys() + 1);
                    ShardingAlgorithmTool.createShardingTable(ShardingTableCacheEnum.of(logicTable),tableName,startTime,endTime);
                    index++;
                }
            }

        });
    }

}
