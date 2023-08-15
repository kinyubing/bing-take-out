package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface DishSetmealMapper {
    /**
     * 根据菜品ids查询套餐id
     * @param dishIds
     * @return
     */

    List<Long> selectByDishId(List<Long> dishIds);
}
