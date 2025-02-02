package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper {
    /**
     * 插入数据
     * @param orders
     */
    void insert(Orders orders);
    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据状态和时间查询订单
     * @param status
     * @param time
     * @return
     */
    @Select("select * from orders where status=#{status} and order_time<#{time}")
    List<Orders> updateByStatusAndTime(Integer status, LocalDateTime time);
    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);
    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据封装的条件进行营业额查询
     * @param map
     * @return
     */
    // select sum(amount) from orders where status=5 and create_time<? and create_time>?
    Double getTurnoverByMap(Map map);

    /**
     * 获取订单每天的订单总数和有效订单总数
     * @param map
     * @return
     */
    Integer getOrderNumByMap(Map map);

    /**
     * 根基指定条件查询出销量前十的商品及其销量
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10List(LocalDateTime begin, LocalDateTime end);
}
