package com.example.msbeerorderservice.sm;

import com.example.msbeerorderservice.domain.BeerOrder;
import com.example.msbeerorderservice.domain.OrderEventEnum;
import com.example.msbeerorderservice.domain.OrderStatusEnum;
import com.example.msbeerorderservice.repositories.BeerOrderRepository;
import com.example.msbeerorderservice.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Component
@RequiredArgsConstructor
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderStatusEnum, OrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;

    @Transactional
    @Override
    public void preStateChange(State<OrderStatusEnum, OrderEventEnum> state, Message<OrderEventEnum> message,
                               Transition<OrderStatusEnum, OrderEventEnum> transition,
                               StateMachine<OrderStatusEnum, OrderEventEnum> stateMachine, StateMachine<OrderStatusEnum, OrderEventEnum> rootStateMachine) {
        log.debug("Pre-State Change");

        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(BeerOrderManagerImpl.ORDER_ID_HEADER, " ")))
                .ifPresent(orderId -> {
                    log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());

                    BeerOrder beerOrder = beerOrderRepository.getOne(UUID.fromString(orderId));
                    beerOrder.setOrderStatus(state.getId());
                    beerOrderRepository.saveAndFlush(beerOrder);
                });
    }
}
