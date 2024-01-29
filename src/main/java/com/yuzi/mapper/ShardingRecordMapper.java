package com.yuzi.mapper;

import com.yuzi.entity.ShardingRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.calcite.adapter.java.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 分片记录表 Mapper 接口
 * </p>
 *
 * @author yuzi
 * @since 2023-12-07
 */
@Mapper
public interface ShardingRecordMapper extends BaseMapper<ShardingRecord> {

    List<ShardingRecord> selectListByLogicTable( @Param("logicTableName") String logicTableName);
}
