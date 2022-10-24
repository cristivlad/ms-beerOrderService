package com.example.msbeerorderservice.services;

import com.example.msbeerorderservice.domain.BeerOrder;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);
}
