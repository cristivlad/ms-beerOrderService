package com.example.msbeerorderservice.sm.actions;

import com.example.msbeerorderservice.config.JmsConfig;
import com.example.msbeerorderservice.domain.BeerOrder;
import com.example.msbeerorderservice.domain.OrderEventEnum;
import com.example.msbeerorderservice.domain.OrderStatusEnum;
import com.example.msbeerorderservice.repositories.BeerOrderRepository;
import com.example.msbeerorderservice.web.mappers.BeerOrderMapper;
import com.example.msbeerorderservice.web.model.events.DeallocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static com.example.msbeerorderservice.services.BeerOrderManagerImpl.ORDER_ID_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeallocateOrderAction implements Action<OrderStatusEnum, OrderEventEnum> {

    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(StateContext<OrderStatusEnum, OrderEventEnum> context) {
        String beerOrderId = (String) context.getMessage().getHeaders().get(ORDER_ID_HEADER);
        Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString(beerOrderId));

        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            jmsTemplate.convertAndSend(JmsConfig.DEALLOCATE_ORDER_QUEUE,
                    DeallocateOrderRequest.builder()
                            .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
                            .build());
            log.debug("Sent Deallocation Request for order id: " + beerOrderId);
        }, () -> log.error("Beer Order Not Found!"));
    }
}
