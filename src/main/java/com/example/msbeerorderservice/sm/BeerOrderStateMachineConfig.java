package com.example.msbeerorderservice.sm;

import com.example.msbeerorderservice.domain.OrderEventEnum;
import com.example.msbeerorderservice.domain.OrderStatusEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderEventEnum> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderEventEnum> states) throws Exception {
        states.withStates()
                .initial(OrderStatusEnum.NEW)
                .states(EnumSet.allOf(OrderStatusEnum.class))
                .end(OrderStatusEnum.PICKED_UP)
                .end(OrderStatusEnum.DELIVERED)
                .end(OrderStatusEnum.CANCELLED)
                .end(OrderStatusEnum.DELIVERY_EXCEPTION)
                .end(OrderStatusEnum.VALIDATION_EXCEPTION)
                .end(OrderStatusEnum.ALLOCATION_EXCEPTION);
    }
}