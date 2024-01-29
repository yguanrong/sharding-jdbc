package com.yuzi.mapper;

import com.yuzi.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.calcite.adapter.java.Map;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author yuzi
 * @since 2023-12-06
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
