package com.yuzi.sharding;

import com.alibaba.druid.util.StringUtils;
import com.yuzi.config.ConfigProperty;
import com.yuzi.entity.ShardingRecord;
import com.yuzi.service.IShardingRecordService;
import com.yuzi.sharding.enums.ShardingTableCacheEnum;
import com.yuzi.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.mode.manager.ContextManager;
import org.apache.shardingsphere.sharding.algorithm.config.AlgorithmProvidedShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.rule.TableRule;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;


/**
 * <p> @Title ShardingAlgorithmTool
 * <p> @Description 按月分片算法工具
 *
 * @author ACGkaka
 * @date 2022/12/20 14:03
 */
@Slf4j
public class ShardingAlgorithmTool {

    private static IShardingRecordService shardingRecordService;

    /** 表分片符号，例：t_user_202201 中，分片符号为 "_" */
    private static final String TABLE_SPLIT_SYMBOL = "_";

    /** 数据库配置 */
    private static final Environment ENV = SpringUtil.getApplicationContext().getEnvironment();
    private static final String DATASOURCE_URL = ENV.getProperty("spring.shardingsphere.datasource.mydb.url");
    private static final String DATASOURCE_USERNAME = ENV.getProperty("spring.shardingsphere.datasource.mydb.username");
    private static final String DATASOURCE_PASSWORD = ENV.getProperty("spring.shardingsphere.datasource.mydb.password");

    /**
     * 初始化分片管理service
     * @param service
     */
    public static void initService(IShardingRecordService service){
        shardingRecordService = service;
    }


    /**
     * 重载全部缓存
     */
    public static void tableNameCacheReloadAll() {
        Arrays.stream(ShardingTableCacheEnum.values()).forEach(ShardingAlgorithmTool::tableNameCacheReload);
    }

    /**
     * 重载指定分表缓存
     * @param logicTable 逻辑表，例：t_user
     */
    public static void tableNameCacheReload(ShardingTableCacheEnum logicTable) {
        // 读取数据库中所有表名
        List<ShardingRecord> tableNameList = getAllTableNameBySchema(logicTable);
        // 更新缓存、配置（原子操作）
        logicTable.atomicUpdateCacheAndActualDataNodes(tableNameList);
        // 删除旧的缓存（如果存在）
        logicTable.resultTableNamesCache().clear();
        // 写入新的缓存
        logicTable.resultTableNamesCache().addAll(tableNameList);
    }

    /**
     * 获取所有表名
     * @return 表名集合
     * @param logicTable 逻辑表
     */
    public static List<ShardingRecord> getAllTableNameBySchema(ShardingTableCacheEnum logicTable) {
        List<ShardingRecord> tableNames = new ArrayList<>();
        try {
            String logicTableName = logicTable.logicTableName();
            tableNames = shardingRecordService.queryAllShardingRecordByLogicName(logicTableName);
        } catch (Exception e) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
        }
        return tableNames;
    }

    /**
     * 动态更新配置 actualDataNodes
     *
     * @param logicTableName  逻辑表名
     * @param tableNamesCache 真实表名集合
     */
    public static void actualDataNodesRefresh(String logicTableName, List<ShardingRecord> tableNamesCache)  {
        try {
            if (CollectionUtils.isEmpty(tableNamesCache)){
                // 防止刚开始初始化的时候，没有表
                return;
            }
            // 获取数据分片节点
            String dbName = "mydb";
            log.info(">>>>>>>>>> 【INFO】更新分表配置，logicTableName:{}，tableNamesCache:{}", logicTableName, tableNamesCache);

            // generate actualDataNodes
            String newActualDataNodes = tableNamesCache.stream().map(ShardingRecord::getActualTable).map(o -> String.format("%s.%s", dbName, o)).collect(Collectors.joining(","));
            ShardingSphereDataSource shardingSphereDataSource = SpringUtil.getBean(ShardingSphereDataSource.class);
            updateShardRuleActualDataNodes(shardingSphereDataSource, logicTableName, newActualDataNodes);
        }catch (Exception e){
            log.error("初始化 动态表单失败，原因：{}", e.getMessage(), e);
        }
    }


    // --------------------------------------------------------------------------------------------------------------
    // 私有方法
    // --------------------------------------------------------------------------------------------------------------


    /**
     * 刷新ActualDataNodes
     */
    private static void updateShardRuleActualDataNodes(ShardingSphereDataSource dataSource, String logicTableName, String newActualDataNodes) {
        // Context manager.
        ContextManager contextManager = dataSource.getContextManager();
        // Rule configuration.
        String schemaName = "logic_db";
        Collection<RuleConfiguration> newRuleConfigList = new LinkedList<>();
        Collection<RuleConfiguration> oldRuleConfigList = dataSource.getContextManager()
                .getMetaDataContexts()
                .getMetaData(schemaName)
                .getRuleMetaData()
                .getConfigurations();

        for (RuleConfiguration oldRuleConfig : oldRuleConfigList) {
            if (oldRuleConfig instanceof AlgorithmProvidedShardingRuleConfiguration) {

                // Algorithm provided sharding rule configuration
                AlgorithmProvidedShardingRuleConfiguration oldAlgorithmConfig = (AlgorithmProvidedShardingRuleConfiguration) oldRuleConfig;
                AlgorithmProvidedShardingRuleConfiguration newAlgorithmConfig = new AlgorithmProvidedShardingRuleConfiguration();

                // Sharding table rule configuration Collection
                Collection<ShardingTableRuleConfiguration> newTableRuleConfigList = new LinkedList<>();
                Collection<ShardingTableRuleConfiguration> oldTableRuleConfigList = oldAlgorithmConfig.getTables();

                oldTableRuleConfigList.forEach(oldTableRuleConfig -> {
                    if (logicTableName.equals(oldTableRuleConfig.getLogicTable())) {
                        ShardingTableRuleConfiguration newTableRuleConfig = new ShardingTableRuleConfiguration(oldTableRuleConfig.getLogicTable(), newActualDataNodes);
                        newTableRuleConfig.setTableShardingStrategy(oldTableRuleConfig.getTableShardingStrategy());
                        newTableRuleConfig.setDatabaseShardingStrategy(oldTableRuleConfig.getDatabaseShardingStrategy());
                        newTableRuleConfig.setKeyGenerateStrategy(oldTableRuleConfig.getKeyGenerateStrategy());

                        newTableRuleConfigList.add(newTableRuleConfig);
                    } else {
                        newTableRuleConfigList.add(oldTableRuleConfig);
                    }
                });

                newAlgorithmConfig.setTables(newTableRuleConfigList);
                newAlgorithmConfig.setAutoTables(oldAlgorithmConfig.getAutoTables());
                newAlgorithmConfig.setBindingTableGroups(oldAlgorithmConfig.getBindingTableGroups());
                newAlgorithmConfig.setBroadcastTables(oldAlgorithmConfig.getBroadcastTables());
                newAlgorithmConfig.setDefaultDatabaseShardingStrategy(oldAlgorithmConfig.getDefaultDatabaseShardingStrategy());
                newAlgorithmConfig.setDefaultTableShardingStrategy(oldAlgorithmConfig.getDefaultTableShardingStrategy());
                newAlgorithmConfig.setDefaultKeyGenerateStrategy(oldAlgorithmConfig.getDefaultKeyGenerateStrategy());
                newAlgorithmConfig.setDefaultShardingColumn(oldAlgorithmConfig.getDefaultShardingColumn());
                newAlgorithmConfig.setShardingAlgorithms(oldAlgorithmConfig.getShardingAlgorithms());
                newAlgorithmConfig.setKeyGenerators(oldAlgorithmConfig.getKeyGenerators());

                newRuleConfigList.add(newAlgorithmConfig);
            }
        }

        // update context
        contextManager.alterRuleConfiguration(schemaName, newRuleConfigList);
    }

    /**
     * 创建分表
     * @param logicTable 逻辑表
     * @param tableName 真实表名，例：t_user_1
     * @return 创建结果（true创建成功，false未创建）
     */
    public static boolean createShardingTable(ShardingTableCacheEnum logicTable, String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        String index = tableName.replace(logicTable.logicTableName() + TABLE_SPLIT_SYMBOL,"");
        synchronized (logicTable.logicTableName().intern()) {
            // 缓存中无此表，则建表并添加缓存
            executeSql(Collections.singletonList("CREATE TABLE IF NOT EXISTS `" + tableName + "` LIKE `" + logicTable.logicTableName() + "`;"));
            // 添加记录
            ShardingRecord shardingRecord = new ShardingRecord();
            shardingRecord.setLogicTable(logicTable.logicTableName());
            shardingRecord.setActualTable(tableName);
            shardingRecord.setIndexNum(Long.valueOf(index));
            shardingRecord.setStartTime(startTime);
            shardingRecord.setEndTime(endTime);
            shardingRecordService.insert(shardingRecord);
            // 缓存重载
            tableNameCacheReload(logicTable);
        }
        return true;
    }

    /**
     * 执行SQL
     * @param sqlList SQL集合
     */
    private static void executeSql(List<String> sqlList) {
        if (StringUtils.isEmpty(DATASOURCE_URL) || StringUtils.isEmpty(DATASOURCE_USERNAME) || StringUtils.isEmpty(DATASOURCE_PASSWORD)) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接配置有误，请稍后重试，URL:{}, username:{}, password:{}", DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD);
            throw new IllegalArgumentException("数据库连接配置有误，请稍后重试");
        }
        try (Connection conn = DriverManager.getConnection(DATASOURCE_URL, DATASOURCE_USERNAME, DATASOURCE_PASSWORD)) {
            try (Statement st = conn.createStatement()) {
                conn.setAutoCommit(false);
                for (String sql : sqlList) {
                    st.execute(sql);
                }
            } catch (Exception e) {
                conn.rollback();
                log.error(">>>>>>>>>> 【ERROR】数据表创建执行失败，请稍后重试，原因：{}", e.getMessage(), e);
                throw new IllegalArgumentException("数据表创建执行失败，请稍后重试");
            }
        } catch (SQLException e) {
            log.error(">>>>>>>>>> 【ERROR】数据库连接失败，请稍后重试，原因：{}", e.getMessage(), e);
            throw new IllegalArgumentException("数据库连接失败，请稍后重试");
        }
    }

}
