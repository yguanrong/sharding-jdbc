package com.yuzi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuzi.dto.QueryParam;
import com.yuzi.entity.User;
import com.yuzi.mapper.UserMapper;
import com.yuzi.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author yuzi
 * @since 2023-12-06
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public List<User> getList(QueryParam param) {
        int start = (param.getPage() - 1) * param.getPageSize();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(User::getCreateTime, param.getStartTime())
                .le(User::getCreateTime, param.getEndTime())
                .orderByDesc(User::getCreateTime)
                .last("limit " + start + "," + param.getPageSize());
        return userMapper.selectList(wrapper);

    }
}
