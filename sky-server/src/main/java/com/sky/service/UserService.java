package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {
    /**
     * 实现小程序登录
     * @return
     */
    User login(UserLoginDTO userLoginDTO);
}
