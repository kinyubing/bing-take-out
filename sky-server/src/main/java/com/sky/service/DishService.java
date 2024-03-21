package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

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
    /**
     * 根据id回显菜品数据
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);
    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    void updateWithFlavor(DishDTO dishDTO);
    /**
     * 根据分类id查询菜品
     *
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);

    /**
     * 根据菜品id和状态对菜品状态进行起售或停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}
