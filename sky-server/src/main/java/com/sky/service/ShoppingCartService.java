package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     * @return
     */
    public void save(ShoppingCartDTO shoppingCartDTO);
    /**
     * 查询当前用户的购物车商品
     * @return
     */
    List<ShoppingCart> list();
    /**
     * 清空购物车
     * @return
     */
    void clean();
    /**
     * 将购物车中的商品减少一个
     * @param shoppingCartDTO
     * @return
     */
    void sub(ShoppingCartDTO shoppingCartDTO);
}
