package com.assignment.amit.recipemanager.controller;

import com.assignment.amit.recipemanager.exceptions.RecipeAlreadyExistException;
import com.assignment.amit.recipemanager.exceptions.RecipeNotFoundException;
import com.assignment.amit.recipemanager.model.Ingredient;
import com.assignment.amit.recipemanager.model.Recipe;
import com.assignment.amit.recipemanager.service.RecipeService;
import com.assignment.amit.recipemanager.testsupport.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeControllerTest {
    @Mock
    private RecipeService recipeService;

    @Test
    public void testAddRecipe() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();
        when(recipeService.addRecipe(any())).thenReturn(recipe);
        Recipe storedRecipe = new RecipeController(recipeService).addRecipe(recipe);
        verify(recipeService, times(1)).addRecipe(any());
        assertEquals(storedRecipe.recipeName(), recipe.recipeName());

    }

    @Test
    public void testAddRecipeWhenAlreadyExist() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();
        when(recipeService.addRecipe(any())).thenThrow(new RecipeAlreadyExistException("Already Exist"));

        RecipeAlreadyExistException ex = assertThrows(RecipeAlreadyExistException.class, () -> {
            new RecipeController(recipeService).addRecipe(recipe);
        });
        verify(recipeService, times(1)).addRecipe(any());

        assertEquals("Already Exist", ex.getMessage());

    }

    @Test
    public void testAddRecipeWhenError() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();
        when(recipeService.addRecipe(any())).thenThrow(new RuntimeException("Internal Error"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            new RecipeController(recipeService).addRecipe(recipe);
        });
        verify(recipeService, times(1)).addRecipe(any());
        assertEquals(HttpStatusCode.valueOf(500), ex.getStatusCode());
        assertEquals("Internal Error", ex.getReason());

    }

    @Test
    public void testGetRecipeWhenNoFilter() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();
        when(recipeService.getAllRecipesWithFiler(any())).thenReturn(List.of(recipe));
        List<Recipe> storedRecipe = new RecipeController(recipeService).getRecipesByFilter(new HashMap<>());
        verify(recipeService, times(1)).getAllRecipesWithFiler(any());
        assertEquals(1, storedRecipe.size());
    }

    @Test
    public void testGetAllRecipeWhenError() {

        when(recipeService.getAllRecipesWithFiler(any())).thenThrow(new RuntimeException("Internal Error"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            new RecipeController(recipeService).getRecipesByFilter(new HashMap<>());
        });
        verify(recipeService, times(1)).getAllRecipesWithFiler(any());
        assertEquals(HttpStatusCode.valueOf(500), ex.getStatusCode());
        assertEquals("Internal Error", ex.getReason());

    }


    @Test
    public void testGetRecipeByName() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        when(recipeService.getRecipe(any())).thenReturn(recipe);
        Recipe storedRecipe = new RecipeController(recipeService).getRecipe("PaneerLababdar").getBody();
        verify(recipeService, times(1)).getRecipe(any());
        assertEquals("PaneerLababdar", Objects.requireNonNull(storedRecipe).recipeName());

    }

    @Test
    public void testGetRecipeByNameWhenNotFound() {
        when(recipeService.getRecipe(any())).thenThrow(new RecipeNotFoundException("Recipe Not Found"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            new RecipeController(recipeService).getRecipe("PaneerLababdar").getBody();
        });
        verify(recipeService, times(1)).getRecipe(any());
        assertEquals(HttpStatusCode.valueOf(404), ex.getStatusCode());
        assertEquals("Recipe Not Found", ex.getReason());

    }

    @Test
    public void testGetRecipeByNameWhenError() {
        when(recipeService.getRecipe(any())).thenThrow(new RuntimeException("Internal Error"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            new RecipeController(recipeService).getRecipe("PaneerLababdar").getBody();
        });
        verify(recipeService, times(1)).getRecipe(any());
        assertEquals(HttpStatusCode.valueOf(500), ex.getStatusCode());
        assertEquals("Internal Error", ex.getReason());

    }


    @Test
    public void testDeleteRecipeByName() {
        doNothing().when(recipeService).deleteRecipe(any());

        ResponseEntity<Recipe> storedRecipe = new RecipeController(recipeService).deleteRecipe("PaneerLababdar");

        verify(recipeService, times(1)).deleteRecipe(any());
        assertEquals(HttpStatus.OK, storedRecipe.getStatusCode());

    }

    @Test
    public void testDeleteRecipeByNameWhenNotFound() {

        doThrow(new RecipeNotFoundException("Recipe Not Found")).when(recipeService).deleteRecipe(any());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            new RecipeController(recipeService).deleteRecipe("PaneerLababdar").getBody();
        });
        verify(recipeService, times(1)).deleteRecipe(any());
        assertEquals(HttpStatusCode.valueOf(404), ex.getStatusCode());
        assertEquals("Recipe Not Found", ex.getReason());

    }

    @Test
    public void testDeleteRecipeByNameWhenError() {
        doThrow(new RuntimeException("Internal Error")).when(recipeService).deleteRecipe(any());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            new RecipeController(recipeService).deleteRecipe("PaneerLababdar").getBody();
        });
        verify(recipeService, times(1)).deleteRecipe(any());
        assertEquals(HttpStatusCode.valueOf(500), ex.getStatusCode());
        assertEquals("Internal Error", ex.getReason());

    }

    @Test
    public void testUpdateRecipeByName() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        when(recipeService.updateRecipe(any(), any())).thenReturn(recipe);

        ResponseEntity<Recipe> storedRecipe = new RecipeController(recipeService).updateRecipe("PaneerLababdar", recipe);

        verify(recipeService, times(1)).updateRecipe(any(), any());
        assertEquals(HttpStatus.OK, storedRecipe.getStatusCode());

    }

    @Test
    public void testUpdateRecipeByNameWhenNotFound() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        doThrow(RecipeNotFoundException.class).when(recipeService).updateRecipe(any(), any());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            new RecipeController(recipeService).updateRecipe("PaneerLababdar", recipe);
        });

        verify(recipeService, times(1)).updateRecipe(any(), any());
        assertEquals(HttpStatusCode.valueOf(404), ex.getStatusCode());
        assertEquals("Recipe Not Found", ex.getReason());
    }

    @Test
    public void testUpdateRecipeByNameWhenError() {
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withId("1").withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withId("1").withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();

        doThrow(RuntimeException.class).when(recipeService).updateRecipe(any(), any());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            new RecipeController(recipeService).updateRecipe("PaneerLababdar", recipe);
        });

        verify(recipeService, times(1)).updateRecipe(any(), any());
        assertEquals(HttpStatusCode.valueOf(500), ex.getStatusCode());
        assertEquals("Internal Error", ex.getReason());

    }
}
