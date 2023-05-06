package com.example.statemachine.listener;

import com.example.statemachine.enums.OrderEvent;
import com.example.statemachine.enums.OrderStatus;
import com.example.statemachine.pojo.Order;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;

@Component
@WithStateMachine(name = "orderStateMachine")
public class OrderStateListener {

    @OnTransition( target = "WAIT_PAYMENT")
    public void init(){
        System.out.println();
    }
    @OnTransition(source = "WAIT_PAYMENT", target = "WAIT_DELIVER")
    public boolean payTransition(Message<OrderEvent> message) {
        Order order = (Order) message.getHeaders().get("order");        // 获取消息中的订单对象
        System.out.println("支付");
        return true;
    }

    @OnTransition(source = "WAIT_DELIVER", target = "WAIT_RECEIVE")
    public boolean deliverTransition(Message<OrderEvent> message) {
        Order order = (Order) message.getHeaders().get("order");
        System.out.println("发货");
        return true;
    }

    @OnTransition(source = "WAIT_RECEIVE", target = "FINISH")
    public boolean receiveTransition(Message<OrderEvent> message){
        Order order = (Order) message.getHeaders().get("order");
        System.out.println("收货" );
        return true;
    }
}
