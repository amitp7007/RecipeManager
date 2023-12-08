package com.assignment.amit.recipemanager.service;

import com.assignment.amit.recipemanager.entity.RecipeEntity;
import com.assignment.amit.recipemanager.model.Ingredient;
import com.assignment.amit.recipemanager.model.Recipe;
import com.assignment.amit.recipemanager.repository.RecipeRepository;
import com.assignment.amit.recipemanager.testsupport.TestUtil;
import com.assignment.amit.recipemanager.util.RecipeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class RecipeServiceTest {

    @Mock
    RecipeRepository repoMock;

    @Mock
    MongoTemplate mongoTemplate;

    @Test
    public void shouldAddRecipeInDB() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();
        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);
        when(repoMock.save(eq(any()))).thenReturn(rEntity);
        RecipeService service = new RecipeService(repoMock, mongoTemplate);
        Recipe recipeReturned = service.addRecipe(recipe);
        Assertions.assertEquals(recipe, recipeReturned);
    }

    @Test
    public void shouldGetAllRecipe() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);

        when(repoMock.findAll()).thenReturn(List.of(rEntity));
        RecipeService service = new RecipeService(repoMock, mongoTemplate);
        List<Recipe> recipeReturned = service.getAllRecipes();
        Assertions.assertEquals(1, recipeReturned.size());
    }
}
