package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    //请求路径
    private static final String url="https://api.weixin.qq.com/sns/jscode2session";
    /**
     * 实现小程序登录
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
         //调用获取openid方法
        String openId = getOpenId(userLoginDTO.getCode());
        //判断获取到的openid是否为空
        if(openId==null){
            //抛出异常
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //获取到的openid不为空，查询数据库查看用户
        User user= userMapper.getUserByOpenid(openId);
        //是否为新（较于小程序登录）用户
        if(user==null){
            //是新用户,构建user,插入数据库数据
            user = User.builder().openid(openId)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        return user;
    }

    /**
     * 通过调用微信接口服务获取用户的openid
     * @param code:微信登录成功后的临时授权码
     * @return ：返回openId
     */
    public String getOpenId(String code){
        Map<String, String> map=new HashMap<>();
        //设置传递的参数
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        //通过调用微信接口服务获取用户的openid
        String json = HttpClientUtil.doGet(url, map);
        //json转换  JSON:fastJson
        JSONObject jsonObject = JSON.parseObject(json);
        //获取openid
        String openid = jsonObject.getString("openid");
        return openid;
    }

}
