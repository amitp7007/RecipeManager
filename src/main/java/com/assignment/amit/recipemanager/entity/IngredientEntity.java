package com.assignment.amit.recipemanager.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@RequiredArgsConstructor
@Getter
@Document
public class IngredientEntity {
	private final String name;
	private final int quantity;
}
