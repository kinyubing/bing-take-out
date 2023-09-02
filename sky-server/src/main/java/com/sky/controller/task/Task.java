package com.sky.controller.task;
import com.sky.entity.Orders;
import com.sky.mapper.OrdersMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component  //bean被Spring容器管理
@Slf4j
public class Task {
    @Autowired
    private OrdersMapper ordersMapper;
    /**
     * 处理超时订单：每一分钟检查一次
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrders(){
        log.info("开始超时订单查询：{}",LocalDateTime.now());
        //获取当前减去15分钟
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        //未支付状态
        List<Orders> list=ordersMapper.updateByStatusAndTime(Orders.PENDING_PAYMENT,time);
        if(list!=null&&list.size()>0){
            //查询到超时订单
            for (Orders orders : list) {
                orders.setStatus(Orders.CANCELLED);//设置状态为取消状态
                orders.setCancelReason("订单超时未支付");//设置订单取消的原因
                orders.setCancelTime(LocalDateTime.now());//设置订单取消的时间
                //进行修改
                ordersMapper.update(orders);
            }
        }
    }

    /**
     * 处理派送中的订单：每天凌晨一点准时处理前一天派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrders(){
        log.info("开始派送中的订单查询：{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> list=ordersMapper.updateByStatusAndTime(Orders.DELIVERY_IN_PROGRESS,time);
        if(list!=null&&list.size()>0){
            //查询到前一天派送中的订单
            for (Orders orders : list) {
                orders.setStatus(Orders.COMPLETED);//设置状态为完成状态
                //进行修改
                ordersMapper.update(orders);
            }
        }
    }
}
