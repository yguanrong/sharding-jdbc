package com.yuzi.sharding;

import com.yuzi.service.IShardingRecordService;
import com.yuzi.sharding.job.TableInitJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * <p> @Title ShardingTablesLoadRunner
 * <p> @Description 项目启动后，读取已有分表，进行缓存
 *
 * @author ACGkaka
 * @date 2022/12/20 15:41
 */
@Component
public class InitializeAfterFlyway implements ApplicationRunner {

    @Autowired
    private IShardingRecordService shardingRecordService;

    @Autowired
    private TableInitJob tableInitJob;

    @Override
    public void run(ApplicationArguments args) {
        // 这里的逻辑将在 Flyway 执行完毕后执行
        System.out.println("Initialization logic after Flyway migrations.");

        // 延迟执行
        ShardingAlgorithmTool.initService(shardingRecordService);

        // 初始化分表
        tableInitJob.createTableAndDeleteExpiredTable();

        // 读取已有分表，进行缓存
        ShardingAlgorithmTool.tableNameCacheReloadAll();
    }
}

