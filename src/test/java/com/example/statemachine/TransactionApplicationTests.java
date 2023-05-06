package com.example.statemachine;

import com.example.statemachine.enums.OrderEvent;
import com.example.statemachine.enums.OrderStatus;
import com.example.statemachine.pojo.Order;
import com.example.statemachine.service.IOrderService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;

import javax.annotation.Resource;

@SpringBootTest
class TransactionApplicationTests {

    @Autowired
    private IOrderService orderService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void contextLoads() throws Exception {

        Order order = orderService.createOrder(1);
        orderService.pay(order);
        System.out.println(order.getStatus());
        orderService.deliver(order);
        orderService.deliver(order);//故意发货两次
        System.out.println(order.getStatus());
        orderService.receive(order);
        System.out.println(order.getStatus());

    }

}
