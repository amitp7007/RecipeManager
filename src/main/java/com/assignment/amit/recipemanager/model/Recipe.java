package com.assignment.amit.recipemanager.model;

import java.util.List;
import java.util.Set;

public record Recipe(String recipeName,
                     List<Ingredient> ingredients,
                     int servings, 
                     String instructions,
                     String isVegetarian) {

}
