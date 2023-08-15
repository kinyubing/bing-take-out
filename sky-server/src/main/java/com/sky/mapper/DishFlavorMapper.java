package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入菜品对应的各种口味
     * @param flavors
     */
    void saveBatch(List<DishFlavor> flavors);

    /**
     * 批量删除菜品对应的口味
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteBatch(Long dishId);
}
