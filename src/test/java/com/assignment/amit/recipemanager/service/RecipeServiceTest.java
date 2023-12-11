package com.assignment.amit.recipemanager.service;

import com.assignment.amit.recipemanager.entity.RecipeEntity;
import com.assignment.amit.recipemanager.exceptions.RecipeNotFoundException;
import com.assignment.amit.recipemanager.model.Ingredient;
import com.assignment.amit.recipemanager.model.Recipe;
import com.assignment.amit.recipemanager.testsupport.TestUtil;
import com.assignment.amit.recipemanager.util.RecipeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
    @Mock
    MongoTemplate mongoTemplate;
    @Test
    public void shouldAddRecipeInDB() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();
        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);
        when(mongoTemplate.save(any())).thenReturn(rEntity);
        RecipeService service = new RecipeService(mongoTemplate);
        Recipe recipeReturned = service.addRecipe(recipe);
        Assertions.assertEquals(recipe, recipeReturned);
    }

    @Test
    public void shouldGetAllRecipe() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);

        when(mongoTemplate.findAll(any())).thenReturn(List.of(rEntity));
        RecipeService service = new RecipeService(mongoTemplate);
        List<Recipe> recipeReturned = service.getAllRecipes();
        Assertions.assertEquals(1, recipeReturned.size());
    }

    @Test
    public void shouldGetRecipeByIdWhenExist() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);

        when(mongoTemplate.findOne(any(), eq(RecipeEntity.class))).thenReturn(rEntity);
        RecipeService service = new RecipeService(mongoTemplate);
        Recipe recipeReturned = service.getRecipe("1");
        Assertions.assertEquals(recipeReturned, recipe);
    }

    @Test
    public void shouldGetRecipeByIdWhenNotExist() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);

        when(mongoTemplate.findOne(any(), eq(RecipeEntity.class))).thenReturn(null);
        assertThrows(RecipeNotFoundException.class, () -> {
            new RecipeService(mongoTemplate).getRecipe("1");
        });
    }

    @Test
    public void shouldDeleteRecipeByIdWhenExist() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);

        when(mongoTemplate.findOne(any(), eq(RecipeEntity.class))).thenReturn(rEntity);
        new RecipeService(mongoTemplate).deleteRecipe("1");
        verify(mongoTemplate, times(1)).findOne(any(), eq(RecipeEntity.class));

    }

    @Test
    public void testDeleteShouldRaiseExceptionRecipeByIdWhenNotExist() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);


        when(mongoTemplate.findOne(any(), eq(RecipeEntity.class))).thenReturn(null);
        assertThrows(RecipeNotFoundException.class, () -> {
            new RecipeService(mongoTemplate).deleteRecipe("1");
        });
        verify(mongoTemplate, times(1)).findOne(any(), eq(RecipeEntity.class));

    }

    @Test
    public void testGetAllRecipeByFilterByNameByServing() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);

        Map<String, String> filterCriteria = new HashMap<>();
        filterCriteria.put("name", "PaneerLababadar");
        filterCriteria.put("serving", "5");

        when(mongoTemplate.find(any(), any())).thenReturn(List.of(rEntity));
        List<Recipe> returnRecipe = new RecipeService(mongoTemplate).getAllRecipesWithFiler(filterCriteria);
        assertEquals(1, returnRecipe.size());
        verify(mongoTemplate, times(1)).find(any(), any());

    }


    @Test
    public void testGetAllRecipeByFilterByIngredientAndTextSearch() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);

        Map<String, String> filterCriteria = new HashMap<>();
        filterCriteria.put("ingredient", "Coriander");
        filterCriteria.put("search", "frying");

        when(mongoTemplate.find(any(), any())).thenReturn(List.of(rEntity));
        List<Recipe> returnRecipe = new RecipeService(mongoTemplate).getAllRecipesWithFiler(filterCriteria);
        assertEquals(1, returnRecipe.size());
        verify(mongoTemplate, times(1)).find(any(), any());

    }

    @Test
    public void testGetAllRecipeByFilterWhenNoFilter() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        RecipeEntity rEntity = RecipeUtil.mapToRecipeEntity(recipe);

        Map<String, String> filterCriteria = new HashMap<>();

        when(mongoTemplate.find(any(), any())).thenReturn(List.of(rEntity));
        List<Recipe> returnRecipe = new RecipeService(mongoTemplate).getAllRecipesWithFiler(filterCriteria);
        assertEquals(1, returnRecipe.size());
        verify(mongoTemplate, times(1)).find(any(), any());

    }
}
