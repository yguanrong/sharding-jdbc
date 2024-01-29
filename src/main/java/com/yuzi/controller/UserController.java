package com.yuzi.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuzi.dto.QueryParam;
import com.yuzi.entity.User;
import com.yuzi.service.IUserService;
import com.yuzi.utils.DateUtil;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author yuzi
 * @since 2023-12-06
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    IUserService userService;

    @PostMapping("/getUserByTime")
    public List<User> getUserByTime(@RequestBody QueryParam param){
        List<User> users = userService.getList(param);
        return users;
    }


    @GetMapping("/saveUser/{num}")
    public String saveUser(@PathVariable("num") Integer num ){
        List<User> users = new ArrayList<>(num);
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < num; i++) {
            int min = 1;
            int maxDay = 10;
            int maxHour = 23;
            int day = random.nextInt(maxDay - min + 1) + min;

            int hour = random.nextInt(maxHour - min + 1) + min;
            LocalDateTime time1 = LocalDateTime.of(
                    now.getYear(),now.getMonth(), day,
                    hour,now.getMinute(),now.getSecond());
            users.add(new User("aaabbb_" + i, "123456", 10, time1, new Date()));
        }
        userService.saveBatch(users);
        return "ok";
    }

}
