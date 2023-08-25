package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 插入user表
     * @param user
     */
    public void insert(User user);

    /**
     * 通过openid查询user表中的user
     *
     * @param openId
     * @return
     */
    @Select("select * from user where openid=#{openId}")
    public User getUserByOpenid(String openId);
}
