package com.msvc.catalog.service.pizza;

import com.msvc.catalog.dto.pizza.request.PizzaRequest;
import com.msvc.catalog.dto.pizza.response.PizzaResponse;

import java.util.List;

public interface PizzaService {

    PizzaResponse createPizza(PizzaRequest request);

    PizzaResponse getPizzaById(Long id);

    List<PizzaResponse> getAllPizzas();

    PizzaResponse updatePizza(Long id, PizzaRequest request);

    void deletePizzaById(Long id);

}
