package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 菜品添加
     * @param dishDTO
     * @return
     */
    @Transactional   //开启事务管理
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //新增菜品
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);//属性拷贝
        dishMapper.save(dish);
        //获取菜品的id(插入操作完成后自动生成的id可以获取出来）
        Long dishId = dish.getId();
        //新增菜品相关的口味
        List<DishFlavor> flavors = dishDTO.getFlavors();//获取口味列表
        if(flavors!=null&&flavors.size()>0){
            //遍历口味表设置每个口味的dishId
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //向口味表中批量插入数据
            dishFlavorMapper.saveBatch(flavors);
        }
    }
}
