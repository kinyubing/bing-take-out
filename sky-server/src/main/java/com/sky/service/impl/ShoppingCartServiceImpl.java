package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    @Override
    public void save(ShoppingCartDTO shoppingCartDTO) {
        //先查看要添加的数据是否已经存在购物车中
        ShoppingCart shoppingCart=new ShoppingCart();//构建购物车对象
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);//属性赋值
        Long userId = BaseContext.getCurrentId();//获取当前用户的id
        shoppingCart.setUserId(userId);
        List<ShoppingCart> list=shoppingCartMapper.list(shoppingCart);//查询数据
        if(list!=null&&list.size()>0){
            //已经存在，只需将数量加1
            ShoppingCart cart = list.get(0);//获取商品数据
            cart.setNumber(cart.getNumber()+1);
            //更新商品数据
            shoppingCartMapper.updateNumber(cart);
        }else{
            //不存在，插入数据
            //查看当前要加入购物车的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            Long setmealId = shoppingCart.getSetmealId();
            if(dishId!=null){
                //当前要加入购物车的是菜品
                //根据菜品id查询菜品数据
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else{
                //当前要加入购物车的是套餐
                //根据套餐id查询套餐数据
                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);//不管是菜品还是套餐第一次加入购物车数量都为1
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }
    /**
     * 查询当前用户的购物车商品
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        //获取当前用户的id
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart= ShoppingCart.builder()
                .userId(userId)
                .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }
    /**
     * 清空购物车
     * @return
     */
    @Override
    public void clean() {
        //获取当前用户的id
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteAll(userId);
    }
    /**
     * 将购物车中的商品减少一个
     * @param shoppingCartDTO
     * @return
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        //封装购物车对象
        ShoppingCart shoppingCart = new ShoppingCart();
        //属性拷贝
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //先查看当前菜品或者套餐在购物中的数量
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);//查询
        ShoppingCart cart = list.get(0);//获取数据
        //获取List中的商品份数
        Integer number = list.get(0).getNumber();
        //判断List中的商品份数
        if (number>1) {
            //数量大于1，进行更新操作
            number--;//获取数量-1
            cart.setNumber(number);//设置数量
            shoppingCartMapper.updateNumber(cart);

        }else {
            //数量<=1，进行删除操作
            shoppingCartMapper.deleteById(cart.getId());
        }
    }
}
