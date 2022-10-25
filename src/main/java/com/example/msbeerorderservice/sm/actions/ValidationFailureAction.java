package com.example.msbeerorderservice.sm.actions;

import com.example.msbeerorderservice.domain.OrderEventEnum;
import com.example.msbeerorderservice.domain.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import static com.example.msbeerorderservice.services.BeerOrderManagerImpl.ORDER_ID_HEADER;

@Slf4j
@Component
public class ValidationFailureAction implements Action<OrderStatusEnum, OrderEventEnum> {

    @Override
    public void execute(StateContext<OrderStatusEnum, OrderEventEnum> stateContext) {
        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(ORDER_ID_HEADER);
        log.error("Compensating transation ... Validation failed: " + beerOrderId);
    }
}
