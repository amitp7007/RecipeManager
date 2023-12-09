package com.assignment.amit.recipemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestRecipeManagerApplication {
	public static void main(String[] args) {
		SpringApplication.from(RecipeManagerApplication::main).with(TestRecipeManagerApplication.class).run(args);
	}

}
