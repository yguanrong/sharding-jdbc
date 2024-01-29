
-- 逻辑表
CREATE TABLE if not exists `t_user` (
     `id` bigint(20) NOT NULL COMMENT '主键',
     `username` varchar(64) NOT NULL COMMENT '用户名',
     `password` varchar(64) NOT NULL COMMENT '密码',
     `age` int(8) NOT NULL COMMENT '年龄',
     `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
     `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- 预制表
CREATE TABLE if not exists `t_user_0` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(64) NOT NULL COMMENT '密码',
    `age` int(8) NOT NULL COMMENT '年龄',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='预制表';

-- 分片记录表
CREATE TABLE if not exists `t_sharding_record` (
    `id` bigint(20) NOT NULL COMMENT '主键',
    `logic_table` varchar(64) NOT NULL COMMENT '逻辑表',
    `actual_table` varchar(64) NOT NULL COMMENT '密码',
    `index_num` bigint(11) NOT NULL COMMENT '表下标',
    `start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
    `end_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='分片记录表';
