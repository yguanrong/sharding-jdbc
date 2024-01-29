package com.yuzi.sharding;

import com.google.common.collect.Range;
import com.yuzi.entity.ShardingRecord;
import com.yuzi.sharding.enums.ShardingTableCacheEnum;
import com.yuzi.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 *分片算法，按月分片
 */
@Slf4j
public class TimeShardingAlgorithm implements StandardShardingAlgorithm<LocalDateTime> {


    /**
     * 表分片符号，例：t_contract_202201 中，分片符号为 "_"
     */
    private final String TABLE_SPLIT_SYMBOL = "_";


    /**
     * 精准分片
     * @param tableNames 对应分片库中所有分片表的集合
     * @param preciseShardingValue 分片键值，其中 logicTableName 为逻辑表，columnName 分片键，value 为从 SQL 中解析出来的分片键的值
     * @return 表名
     */
    @Override
    public String doSharding(Collection<String> tableNames, PreciseShardingValue<LocalDateTime> preciseShardingValue) {
        String logicTableName = preciseShardingValue.getLogicTableName();
        ShardingTableCacheEnum logicTable = ShardingTableCacheEnum.of(logicTableName);
        if (logicTable == null) {
            log.error(">>>>>>>>>> 【ERROR】数据表类型错误，请稍后重试，logicTableNames：{}，logicTableName:{}",
                    ShardingTableCacheEnum.logicTableNames(), logicTableName);
            throw new IllegalArgumentException("数据表类型错误，请稍后重试");
        }

        // 判断命中的分表
        LocalDateTime dateTime = preciseShardingValue.getValue();
        String resultTableName = getActualTable(logicTable, dateTime);

        if (StringUtils.isBlank(resultTableName)){
            // 如果分表不存在就返回默认表
            resultTableName =  logicTableName + TABLE_SPLIT_SYMBOL + "0";
        }
        // 打印分片信息
        log.info(">>>>>>>>>> 【INFO】精确分片，逻辑表：{}，物理表：{}", logicTableName, resultTableName);
        return resultTableName;
    }

    /**
     * 范围分片
     * @param tableNames 对应分片库中所有分片表的集合
     * @param rangeShardingValue 分片范围
     * @return 表名集合
     */
    @Override
    public Collection<String> doSharding(Collection<String> tableNames, RangeShardingValue<LocalDateTime> rangeShardingValue) {
        String logicTableName = rangeShardingValue.getLogicTableName();
        ShardingTableCacheEnum logicTable = ShardingTableCacheEnum.of(logicTableName);
        if (logicTable == null) {
            log.error(">>>>>>>>>> 【ERROR】逻辑表范围异常，请稍后重试，logicTableNames：{}，logicTableName:{}",
                    ShardingTableCacheEnum.logicTableNames(), logicTableName);
            throw new IllegalArgumentException("逻辑表范围异常，请稍后重试");
        }

        // 循环计算分表范围
        Set<String> resultTableNames = getActualTableSet(logicTable,rangeShardingValue.getValueRange());
        if (CollectionUtils.isEmpty(resultTableNames)){
            // 不存在就返回默认表
            resultTableNames = new HashSet<>();
            resultTableNames.add(logicTableName + TABLE_SPLIT_SYMBOL + "0");
        }
        // 打印分片信息
        log.info(">>>>>>>>>> 【INFO】范围分片，逻辑表：{}，物理表：{}", logicTableName, resultTableNames);
        return resultTableNames;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void init() {

    }

    // --------------------------------------------------------------------------------------------------------------
    // 私有方法
    // --------------------------------------------------------------------------------------------------------------

    /**
     * 获取 最小分片值
     * @param tableNames 表名集合
     * @return 最小分片值
     */
    private LocalDateTime getLowerEndpoint(Collection<ShardingRecord> tableNames) {
        Optional<LocalDateTime> optional = tableNames.stream()
                .map(ShardingRecord::getStartTime)
                .min(Comparator.comparing(Function.identity()));
        if (optional.isPresent()) {
            return optional.get();
        } else {
            log.error(">>>>>>>>>> 【ERROR】获取数据最小分表失败，请稍后重试，tableName：{}", tableNames);
            throw new IllegalArgumentException("获取数据最小分表失败，请稍后重试");
        }
    }

    /**
     * 获取 最大分片值
     * @param tableNames 表名集合
     * @return 最大分片值
     */
    private LocalDateTime getUpperEndpoint(Collection<ShardingRecord> tableNames) {
        Optional<LocalDateTime> optional = tableNames.stream()
                .map(ShardingRecord::getEndTime)
                .max(Comparator.comparing(Function.identity()));
        if (optional.isPresent()) {
            return optional.get();
        } else {
            log.error(">>>>>>>>>> 【ERROR】获取数据最大分表失败，请稍后重试，tableName：{}", tableNames);
            throw new IllegalArgumentException("获取数据最大分表失败，请稍后重试");
        }
    }

    private String getActualTable(ShardingTableCacheEnum logicTable, LocalDateTime dateTime) {
        AtomicReference<String> tableName = new AtomicReference<>();
        long dateTimeLong = DateUtil.localDateTimeToLong(dateTime);
        logicTable.resultTableNamesCache().forEach(shardingRecord -> {
            if (DateUtil.localDateTimeToLong(shardingRecord.getStartTime()) <= dateTimeLong &&
                    DateUtil.localDateTimeToLong(shardingRecord.getEndTime()) >= dateTimeLong){
                tableName.set(shardingRecord.getActualTable());
            }
        });
        return tableName.get();
    }

    private Set<String> getActualTableSet(ShardingTableCacheEnum logicTable, Range<LocalDateTime> valueRange) {
        Set<String> tableNameList = new HashSet<>();
        // between and 的起始值
        boolean hasLowerBound = valueRange.hasLowerBound();
        boolean hasUpperBound = valueRange.hasUpperBound();

        // 获取最大值和最小值
        Set<ShardingRecord> tableNameCache = logicTable.resultTableNamesCache();
        LocalDateTime min = hasLowerBound ? valueRange.lowerEndpoint() :getLowerEndpoint(tableNameCache);
        LocalDateTime max = hasUpperBound ? valueRange.upperEndpoint() :getUpperEndpoint(tableNameCache);

        long minLong = DateUtil.localDateTimeToLong(min);
        long maxLong =  DateUtil.localDateTimeToLong(max);

        logicTable.resultTableNamesCache().forEach(shardingRecord -> {
            long startTimeLong = DateUtil.localDateTimeToLong(shardingRecord.getStartTime());
            long endTimeLong = DateUtil.localDateTimeToLong(shardingRecord.getEndTime());
            if (!(maxLong < startTimeLong || endTimeLong < minLong)){
                // 有相交
                tableNameList.add(shardingRecord.getActualTable());
            }
        });
        return tableNameList;
    }
}

