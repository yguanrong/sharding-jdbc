package com.yuzi.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ConfigProperty {

    /**
     * 单表的时间跨度
     */
    @Value("${sharding.time.days:3}")
    private int shardingTimeDys;

    /**
     * 分表的开始时间
     */
    @Value("${sharding.time.start:2023-12-01 00:00:00}")
    private String shardingStartTime;
}
