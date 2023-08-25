package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户相关接口")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    //自动注入jwt属性bean
    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 实现小程序登录
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("小程序登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信登录的临时登录凭证code为：{}",userLoginDTO.getCode());
        //调用service层方法实现功能
        User user=userService.login(userLoginDTO);
        //创建令牌jwt
        Map<String, Object> claims=new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        //创建UserVo对象（建造者模式）
        UserLoginVO userLoginVO=UserLoginVO.builder().id(user.getId())
                .openid(user.getOpenid())
                .token(jwt)
                .build();
        return Result.success(userLoginVO);
    }
}
