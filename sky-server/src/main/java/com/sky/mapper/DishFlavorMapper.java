package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入菜品对应的各种口味
     * @param flavors
     */
    void saveBatch(List<DishFlavor> flavors);

    /**
     * 根据dishId批量删除菜品对应的口味
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteBatch(Long dishId);

    /**
     * 根据dishId查询口味数据
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    /**
     * 根据dishIds批量删除口味
     * @param ids
     */
    void deleteBatchByDishIds(List<Long> dishIds);
}
