package com.assignment.amit.recipemanager.model;

import java.util.List;

public record Recipe(String id, 
                     String name, 
                     List<Ingredient> ingredients, 
                     int servings, 
                     String instructions,
                     String isVegetarian) {

}
