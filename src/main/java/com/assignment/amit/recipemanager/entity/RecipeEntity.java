package com.assignment.amit.recipemanager.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@RequiredArgsConstructor
@Document
@Getter
public class RecipeEntity {
	final String id;
	final String recipeName;
	final int servings;
	final List<IngredientEntity> ingredients;
	final String instructions;
	final String isVegetarian;
}
