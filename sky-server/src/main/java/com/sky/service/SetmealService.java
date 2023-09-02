package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 根据分类id查询套餐
     *
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);
    /**
     * 根据套餐id查询包含的菜品列表
     *
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    void saveWithDish(SetmealDTO setmealDTO);
    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    void deleteBatch(List<Long> ids);
    /**
     * 根据id查询套餐和关联的菜品数据
     * @param id
     * @return
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);
    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}
