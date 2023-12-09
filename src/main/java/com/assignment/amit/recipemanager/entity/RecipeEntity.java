package com.assignment.amit.recipemanager.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;


@Document
@AllArgsConstructor
@Data
public class RecipeEntity {
	@Id
	private String id;
	@Indexed(unique = true)
	private String recipeName;
	private int servings;
	private List<IngredientEntity> ingredients;
	private String instructions;
	private String isVegetarian;
	//private String createdBy;

}
