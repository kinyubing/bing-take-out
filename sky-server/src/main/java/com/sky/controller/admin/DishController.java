package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    //自动装配
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 菜品添加
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加菜品")
    public Result saveWithFlavor(@RequestBody DishDTO dishDTO){
        dishService.saveWithFlavor(dishDTO);
        //菜品添加只会影响该菜品相关联的分类，只清理该分类下的缓存
        String key="dish_"+dishDTO.getCategoryId();
        cleanCache(key);
        return Result.success();
    }

    /**
     * 菜品分类查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分类查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult=dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result deleteWithFlavor(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        //菜品批量删除会影响到多个分类，所以删除所有的缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据id回显菜品数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id回显菜品数据")
    public Result<DishVO> getByIdWithFlavor(@PathVariable Long id){
        DishVO dishVO=dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateWithFlavor(@RequestBody DishDTO dishDTO)
    {
        dishService.updateWithFlavor(dishDTO);
        //修改菜品有可能会影响两个分类，所以删除所有的缓存
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据传入的模式清理缓存
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);//获取所有符合该模式的key
        redisTemplate.delete(keys);
    }
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    /**
     * 根据菜品id和状态对菜品状态进行起售或停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "菜品起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status,id);
        //起售或停售此时并不知道所关联的分类id,还要查询数据库，所以删除所有的缓存
        cleanCache("dish_*");
        return Result.success();
    }
}
