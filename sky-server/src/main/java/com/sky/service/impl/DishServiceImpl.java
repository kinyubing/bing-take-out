package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishSetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private DishSetmealMapper dishSetmealMapper;
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
    /**
     * 菜品分类查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        Page<DishVO> page=dishMapper.page(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }
    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @Override
    public void deleteWithFlavor(List<Long> ids) {
        //1.判断菜品的状态，起售中的菜品不能删除
        for (Long id : ids) {
            //根据id查询菜品
            Dish dish=dishMapper.getById(id);
            if(dish.getStatus()== StatusConstant.ENABLE){
                //菜品处于起售中不能删除，抛出异常
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //2.判断菜品是否关联了相关套餐，关联了相关套餐的菜品不能删除
        List<Long> setmealIds=dishSetmealMapper.selectByDishId(ids);
        if(setmealIds!=null&&setmealIds.size()>0){
            //菜品关联了相关套餐不能删除，抛出异常
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3.删除菜品+删除菜品所对应的口味
        for (Long id : ids) {
            //删除菜品
            dishMapper.deleteById(id);
            //批量删除对应的口味
            dishFlavorMapper.deleteBatch(id);
        }

    }
    /**
     * 根据id回显菜品数据
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据dishId查询相关口味列表
        List<DishFlavor> flavors=dishFlavorMapper.getByDishId(id);
        //新建一个DishVo对象
        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }
    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //修改菜品对应的口味表
        //1.删除菜品对应的所有口味
        dishFlavorMapper.deleteBatch(dish.getId());
        //2.插入菜品对应的所有口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null&&flavors.size()>0){
            //遍历口味表设置每个口味的dishId
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            //向口味表中批量插入数据
            dishFlavorMapper.saveBatch(flavors);
        }

    }
    /**
     * 根据分类id查询菜品
     *
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
