package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

public interface DishService {
    /**
     * 菜品添加
     * @param dishDTO
     * @return
     */
    void saveWithFlavor(DishDTO dishDTO);
    /**
     * 菜品分类查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);
    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    void deleteWithFlavor(List<Long> ids);
}
