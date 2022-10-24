package com.example.msbeerorderservice.services;

import com.example.msbeerorderservice.domain.BeerOrder;
import com.example.msbeerorderservice.domain.OrderEventEnum;
import com.example.msbeerorderservice.domain.OrderStatusEnum;
import com.example.msbeerorderservice.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.msbeerorderservice.domain.OrderStatusEnum.NEW;

@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    private final StateMachineFactory<OrderStatusEnum, OrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(NEW);

        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
        sendBeerOrderEvent(savedBeerOrder, OrderEventEnum.VALIDATE_ORDER);

        return savedBeerOrder;
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, OrderEventEnum eventEnum) {
        StateMachine<OrderStatusEnum, OrderEventEnum> sm = build(beerOrder);

        Message msg = MessageBuilder.withPayload(eventEnum)
                .build();

        sm.sendEvent(msg);
    }

    private StateMachine<OrderStatusEnum, OrderEventEnum> build(BeerOrder beerOrder) {
        StateMachine<OrderStatusEnum, OrderEventEnum> sm = stateMachineFactory.getStateMachine(beerOrder.getId());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null)));

        sm.start();
        return sm;
    }
}
