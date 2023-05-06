package com.example.statemachine.service.impl;

import com.example.statemachine.enums.OrderEvent;
import com.example.statemachine.enums.OrderStatus;
import com.example.statemachine.pojo.Order;
import com.example.statemachine.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderServiceImpl implements IOrderService {

    @Resource
    private StateMachine<OrderStatus, OrderEvent> orderStateMachine;

    @Autowired
    private StateMachinePersister<OrderStatus, OrderEvent, Order> persister;

    private Map<Integer, Order> orders = new HashMap<>();

    public Order createOrder(int id) {
        Order order = new Order();
        order.setStatus(OrderStatus.WAIT_PAYMENT);
        order.setId(id);
        return order;
    }

    public void pay(Order order) {
        Message message = MessageBuilder.withPayload(OrderEvent.PAYED).
                setHeader("order", order).build();
        if (!sendEvent(message, order)) {
            System.out.println("线程：" + Thread.currentThread().getName() + " 支付失败, 状态异常，订单号：" + order.getId());
        }
    }

    /**
     * 发货
     * @param order
     */
    public void deliver(Order order) {
        Message message = MessageBuilder.withPayload(OrderEvent.DELIVERY).
                setHeader("order", order).build();
        if (!sendEvent(message, order)) {
            System.out.println("线程：" + Thread.currentThread().getName() + " 发货失败，状态异常，订单号：" + order.getId());
        }
    }

    public void receive(Order order) {
        Message message = MessageBuilder.withPayload(OrderEvent.RECEIVED).
                setHeader("order", order).build();
        if (!sendEvent(message, order)) {
            System.out.println("线程：" + Thread.currentThread().getName() + " 收货失败，状态异常，订单号：" + order.getId());
        }
    }


    /**
     * 发送订单状态转换事件
     * @param message
     * @param order
     */
    private synchronized boolean sendEvent(Message<OrderEvent> message, Order order) {
        boolean result = false;
        try {
            orderStateMachine.start();
            //尝试恢复状态机状态()
            persister.restore(orderStateMachine, order);
            //添加延迟用于线程安全测试
//            Thread.sleep(500);
            result = orderStateMachine.sendEvent(message);
            //持久化状态机状态
            persister.persist(orderStateMachine, order);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            orderStateMachine.stop();
        }
        return result;
    }
}
