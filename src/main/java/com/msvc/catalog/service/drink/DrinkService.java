package com.msvc.catalog.service.drink;

import com.msvc.catalog.dto.drink.request.DrinkRequest;
import com.msvc.catalog.dto.drink.response.DrinkResponse;

import java.util.List;

public interface DrinkService {

    DrinkResponse createDrink(DrinkRequest request);

    DrinkResponse getDrinkById(Long id);

    List<DrinkResponse> getAllDrinks();

    DrinkResponse updateDrink(Long id, DrinkRequest request);

    void deleteDrinkById(Long id);

}
