package com.assignment.amit.recipemanager.util;

import com.assignment.amit.recipemanager.entity.IngredientEntity;
import com.assignment.amit.recipemanager.entity.RecipeEntity;
import com.assignment.amit.recipemanager.model.Ingredient;
import com.assignment.amit.recipemanager.model.Recipe;

import java.util.List;

public class RecipeUtil {
    public static RecipeEntity mapToRecipeEntity(Recipe recipe) {
        List<IngredientEntity> ingredientEntities = mapToIngredientsEntity(recipe.ingredients());
        RecipeEntity entity = new RecipeEntity(recipe.id(), recipe.name(), recipe.servings(),
                ingredientEntities, recipe.instructions(), recipe.isVegetarian());
        return entity;

    }

    public static List<IngredientEntity> mapToIngredientsEntity(List<Ingredient> ingredients) {
        return ingredients.stream().map(ingredient -> {
            return new IngredientEntity(ingredient.name(), ingredient.quantity());
        }).toList();

    }

    public static List<Ingredient> mapToIngredients(List<IngredientEntity> ingredientEntities) {
        return ingredientEntities.stream().map(ingredientEntity -> {
            return new Ingredient(ingredientEntity.getName(), ingredientEntity.getQuantity());
        }).toList();
    }

    public static Recipe maoToRecipe(RecipeEntity entity) {
        return new Recipe(entity.getId(), entity.getRecipeName(),
                mapToIngredients(entity.getIngredients()), entity.getServings(),
                entity.getInstructions(), entity.getIsVegetarian());
    }
}
