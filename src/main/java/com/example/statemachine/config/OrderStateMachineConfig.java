package com.example.statemachine.config;

import com.example.statemachine.enums.OrderEvent;
import com.example.statemachine.enums.OrderStatus;
import com.example.statemachine.pojo.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import java.util.EnumSet;

/**
 * 状态流转配置
 */
@Configuration
@EnableStateMachine(name = "orderStateMachine")
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatus, OrderEvent> {

    /**
     * 配置状态
     *
     * @param states
     */
    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states.withStates().initial(OrderStatus.WAIT_PAYMENT)  //初始状态
                .states(EnumSet.allOf(OrderStatus.class));   //声明所有状态
    }

    /**
     * 配置状态事件转换关系
     *
     * @param transitions
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStatus.WAIT_PAYMENT).target(OrderStatus.WAIT_DELIVER)
                .event(OrderEvent.PAYED)
                .and()
                .withExternal().source(OrderStatus.WAIT_DELIVER).target(OrderStatus.WAIT_RECEIVE)
                .event(OrderEvent.DELIVERY)
                .and()
                .withExternal().source(OrderStatus.WAIT_RECEIVE).target(OrderStatus.FINISH)
                .event(OrderEvent.RECEIVED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderStatus, OrderEvent> config) throws Exception {
        config.withConfiguration().machineId("orderStateMachine");
    }

    /**
     * 持久化配置
     * 在实际使用中，可以配合Redis等进行持久化操作
     */
    @Bean
    public DefaultStateMachinePersister persister() {
        return new DefaultStateMachinePersister<>(new StateMachinePersist<OrderStatus, OrderEvent, Order>() {
            @Override
            public void write(StateMachineContext<OrderStatus, OrderEvent> context, Order order) throws Exception {
                order.setStatus(context.getState());    //将状态机的最新状态赋值给业务对象
                //TODO 省略持久化操作，如数据库、Redis等
            }

            @Override
            public StateMachineContext<OrderStatus, OrderEvent> read(Order order) throws Exception {
                //此处直接获取Order中的状态，其实并没有在其他介质中进行读取操作
                return new DefaultStateMachineContext(order.getStatus(), null, null, null);
            }
        });
    }


}
