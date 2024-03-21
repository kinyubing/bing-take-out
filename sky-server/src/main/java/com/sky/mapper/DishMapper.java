package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {
    
    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    /**
     * 菜品添加
     * @param dish
     * @return
     */
    @AutoFill(value = OperationType.INSERT)  //公共字段自动填充
    void save(Dish dish);
    /**
     * 菜品分类查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * //根据id查询菜品
     * @param id
     * @return
     */
    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);

    /**
     * 根据id删除菜品
     * @param id
     */
    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);
    /**
     * 修改菜品
     * @param dish
     * @return
     */
    void update(Dish dish);

    /**
     * 根据分类id查询相关联的菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);
    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

        /**
         * 根据条件统计菜品数量
         * @param map
         * @return
         */
        Integer countByMap(Map map);

    /**
     * 根据菜品id批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);
}
