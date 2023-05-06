package com.example.statemachine.service;

import com.example.statemachine.pojo.Order;

import java.util.Map;

public interface IOrderService {
    //创建新订单
    Order createOrder(int id);
    //发起支付
    void pay(Order order);
    /**
     * 发货
     * @param order
     */
    void deliver(Order order);

    /**
     * 订单收货
     * @param order
     */
    void receive(Order order);
}
