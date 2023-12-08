package com.assignment.amit.recipemanager.testsupport;

import com.assignment.amit.recipemanager.model.Ingredient;
import com.assignment.amit.recipemanager.model.Recipe;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

public class TestUtil {


    @NoArgsConstructor
    public static  class TestIngredientBuilder {
        String id;
        String name;
        int quantity;
        public static TestIngredientBuilder newBuilder(){
            return new TestIngredientBuilder();
        }
        public Ingredient build(){
            return new Ingredient(this.name, this.quantity);
        }
        public TestIngredientBuilder withId(String id){
            this.id = id;
            return this;
        }
        public TestIngredientBuilder withName(String name){
            this.name = name;
            return this;
        }
        public TestIngredientBuilder withQuantity(int quantity){
            this.quantity = quantity;
            return this;
        }
    }
    @NoArgsConstructor
    public  static class TestRecipeBuilder{
            String id;
            String name;
            List<Ingredient> ingredients;
            int servings;
            String instructions;
             String isVegeterian;

        public static  TestRecipeBuilder newBuilder(){
                return new TestRecipeBuilder();
            }
            public Recipe build() {
                return new Recipe(this.id, this.name,
                        this.ingredients, this.servings,
                        this.instructions, this.isVegeterian);
            }
            public TestRecipeBuilder withId(String id){
                this.id = id;
                return this;
            }
            public TestRecipeBuilder withName(String name){
                this.name = name;
                return this;
            }
        public TestRecipeBuilder withIngredients(List<Ingredient> ingredients){
            this.ingredients = ingredients;
            return this;
        }
        public TestRecipeBuilder withServing(int numberOfService){
            this.servings = numberOfService;
            return this;
        }
        public TestRecipeBuilder withInstructions(String instructions){
            this.instructions = instructions;
            return this;
        }
        public TestRecipeBuilder isVegetarian(String isVegetarian){
            this.isVegeterian = isVegetarian;
            return this;
        }
    }

    @Builder
    public static class TestIngredients{
        String id;
        String name;
        int quantity;
    }

}
