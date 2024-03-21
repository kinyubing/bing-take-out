package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")  //指定容器中生成的bean的名字
@Api(tags = "管理端店铺相关接口")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {
    //设置营业状态常量
    private static final String KEY="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 设置店铺营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺设状态为：{}",status==1?"营业中":"打烊中");
        //将数据存储在redis中，因为数据只有一行一列存储一张表浪费空间
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getStatus(){
        //从redis获取营业状态
        Integer status = (Integer)redisTemplate.opsForValue().get(KEY);
        log.info("获取店铺设状态为：{}",status==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
