package com.yuzi.service;

import com.yuzi.dto.QueryParam;
import com.yuzi.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author yuzi
 * @since 2023-12-06
 */
public interface IUserService extends IService<User> {

    List<User> getList(QueryParam param);

}
