package com.assignment.amit.recipemanager;

import com.assignment.amit.recipemanager.entity.RecipeEntity;
import com.assignment.amit.recipemanager.model.Ingredient;
import com.assignment.amit.recipemanager.model.Recipe;
import com.assignment.amit.recipemanager.testsupport.TestMongoConfiguration;
import com.assignment.amit.recipemanager.testsupport.TestUtil;
import com.assignment.amit.recipemanager.util.RecipeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Objects;

@ExtendWith(SpringExtension.class)
@DirtiesContext
@SpringBootTest(classes = RecipeManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration")
@Import(TestMongoConfiguration.class)
public class RecipeManagerITTest {
    @LocalServerPort
    private int port;
    @Autowired
    private MongoTemplate mongoTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    String createUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @AfterEach
    public void cleanup() {
        mongoTemplate.dropCollection(RecipeEntity.class);
    }

    @Test
    public void testGetRecipeWhenNoRecipe() throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createUrl("/recipes"),
                HttpMethod.GET, entity, String.class);

        String expected = "[]";

        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

    @Test
    public void testGetRecipeMultipleRecipe() throws JSONException, JsonProcessingException {
        //given
        Ingredient coriander =
                createIngredient("Coriander", 2);
        Ingredient paneer =
                createIngredient("Paneer", 2);
        Recipe paneerRecipe = createRecipe("PaneerLababdar", 5, List.of(coriander, paneer), "Start with frying ", "true");

        Ingredient chicken =
                createIngredient("Chicken", 5);

        Recipe chickenRecipe = createRecipe("ChickenTikka", 4, List.of(coriander, chicken), "Marinate and put in the " +
                        "oven",
                "false");

        Recipe chickenCurry = createRecipe("ChickenCurry", 4, List.of(coriander, chicken), "Marinate and Boil for 1 " +
                        "hours",
                "false");

        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(paneerRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenCurry));

        List<Recipe> expected = List.of(paneerRecipe, chickenRecipe, chickenCurry);

        //when
        HttpHeaders headers = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                createUrl("/recipes"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>() {
                });

        //then
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void testGetRecipeByFilterAllNonVegetarian() throws JSONException, JsonProcessingException {
        //given
        Ingredient coriander =
                createIngredient("Coriander", 2);
        Ingredient paneer =
                createIngredient("Paneer", 2);
        Recipe paneerRecipe = createRecipe("PaneerLababdar", 5, List.of(coriander, paneer), "Start with frying ", "true");

        Ingredient chicken =
                createIngredient("Chicken", 5);

        Recipe chickenRecipe = createRecipe("ChickenTikka", 4, List.of(coriander, chicken), "Marinate and put in the " +
                        "oven",
                "false");

        Recipe chickenCurry = createRecipe("ChickenCurry", 4, List.of(coriander, chicken), "Marinate and Boil for 1 " +
                        "hours",
                "false");

        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(paneerRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenCurry));

        List<Recipe> expected = List.of(chickenRecipe, chickenCurry);

        //when
        HttpHeaders headers = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                createUrl("/recipes?isVegetarian=false"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>() {
                });

        //then
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
    }


    @Test
    public void testGetRecipeByFilterAllWithNoChickenAsIngredient() throws JSONException, JsonProcessingException {
        //given
        Ingredient coriander =
                createIngredient("Coriander", 2);
        Ingredient paneer =
                createIngredient("Paneer", 2);
        Recipe paneerRecipe = createRecipe("PaneerLababdar", 5, List.of(coriander, paneer), "Start with frying ", "true");

        Ingredient chicken =
                createIngredient("Chicken", 5);

        Recipe chickenRecipe = createRecipe("ChickenTikka", 4, List.of(coriander, chicken), "Marinate and put in the " +
                        "oven",
                "false");

        Recipe chickenCurry = createRecipe("ChickenCurry", 4, List.of(coriander, chicken), "Marinate and Boil for 1 " +
                        "hours",
                "false");

        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(paneerRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenCurry));

        List<Recipe> expected = List.of(paneerRecipe);

        //when
        HttpHeaders headers = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                createUrl("/recipes?ingredient=!Chicken"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>() {
                });

        //then
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
    }


    @Test
    public void testGetRecipeByFilterAllWithChickenAndInstructionInOven() throws JSONException,
            JsonProcessingException {
        //given
        Ingredient coriander =
                createIngredient("Coriander", 2);
        Ingredient paneer =
                createIngredient("Paneer", 2);
        Recipe paneerRecipe = createRecipe("PaneerLababdar", 5, List.of(coriander, paneer), "Start with frying ", "true");

        Ingredient chicken =
                createIngredient("Chicken", 5);

        Recipe chickenRecipe = createRecipe("ChickenTikka", 4, List.of(coriander, chicken), "Marinate and put in the " +
                        "oven",
                "false");

        Recipe chickenCurry = createRecipe("ChickenCurry", 4, List.of(coriander, chicken), "Marinate and Boil for 1 " +
                        "hours",
                "false");

        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(paneerRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenCurry));

        List<Recipe> expected = List.of(chickenRecipe);

        //when
        HttpHeaders headers = new HttpHeaders();
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                createUrl("/recipes?ingredient=Chicken&instructions=oven*"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>() {
                });

        //then
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertEquals(expected.size(), Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    public void testUpdateRecipeNumberOfServingSearchByName() throws JSONException,
            JsonProcessingException {
        //given
        Ingredient coriander =
                createIngredient("Coriander", 2);
        Ingredient paneer =
                createIngredient("Paneer", 2);
        Recipe paneerRecipe = createRecipe("PaneerLababdar", 5, List.of(coriander, paneer), "Start with frying ", "true");

        Ingredient chicken =
                createIngredient("Chicken", 5);

        Recipe chickenRecipe = createRecipe("ChickenTikka", 4, List.of(coriander, chicken), "Marinate and put in the " +
                        "oven",
                "false");

        Recipe chickenCurry = createRecipe("ChickenCurry", 4, List.of(coriander, chicken), "Marinate and Boil for 1 " +
                        "hours",
                "false");

        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(paneerRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenCurry));

        //List<Recipe> expected = List.of(chickenRecipe);

        //when
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>("{ \"servings\" : 10}", headers);

        ResponseEntity<Recipe> response = restTemplate.exchange(
                createUrl("/recipes/ChickenTikka"),
                HttpMethod.PUT, entity, new ParameterizedTypeReference<Recipe>() {
                });

        //then
        entity = new HttpEntity<>(null, headers);
        ResponseEntity<Recipe> responseAfterUpdate = restTemplate.exchange(
                createUrl("/recipes/ChickenTikka"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<Recipe>() {
                });
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        Assertions.assertNotEquals(chickenRecipe.servings(),
                Objects.requireNonNull(responseAfterUpdate.getBody()).servings());
        Assertions.assertEquals(10,
                Objects.requireNonNull(responseAfterUpdate.getBody()).servings());
    }
    @Test
    public void testAddRecipe() throws JSONException, JsonProcessingException {
        //given
        List<Ingredient> ingredient = List.of(createIngredient("Coriander", 2));
        Recipe recipe = createRecipe("PaneerLababdar", 5, ingredient, "Start with frying the paneer", "true");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(recipe), headers);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
                createUrl("/recipes"),
                HttpMethod.POST, entity, String.class);

        //then
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
    }

    @Test
    public void testDeleteRecipeWhenExist() throws JSONException, JsonProcessingException {
        //given
        Ingredient coriander =
                createIngredient("Coriander", 2);
        Ingredient paneer =
                createIngredient("Paneer", 2);
        Recipe paneerRecipe = createRecipe("PaneerLababdar", 5, List.of(coriander, paneer), "Start with frying ", "true");

        Ingredient chicken =
                createIngredient("Chicken", 5);

        Recipe chickenRecipe = createRecipe("ChickenTikka", 4, List.of(coriander, chicken), "Marinate and put in the " +
                        "oven",
                "false");

        Recipe chickenCurry = createRecipe("ChickenCurry", 4, List.of(coriander, chicken), "Marinate and Boil for 1 " +
                        "hours",
                "false");

        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(paneerRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenRecipe));
        mongoTemplate.save(RecipeUtil.mapToRecipeEntity(chickenCurry));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        //before delete GET
        ResponseEntity<List<Recipe>> responseBeforeDelete = restTemplate.exchange(
                createUrl("/recipes"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>(){});

        Assertions.assertEquals(3, responseBeforeDelete.getBody().size());

        //when delete
        ResponseEntity<List<Recipe>> response = restTemplate.exchange(
                createUrl("/recipes/PaneerLababdar"),
                HttpMethod.DELETE, entity, new ParameterizedTypeReference<List<Recipe>>(){});

        //then after delete
        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        ResponseEntity<List<Recipe>> responseAfterDelete = restTemplate.exchange(
                createUrl("/recipes"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<List<Recipe>>(){});
        ResponseEntity<Recipe> responseForDeletedRecipe = restTemplate.exchange(
                createUrl("/recipes/PaneerLababdar"),
                HttpMethod.GET, entity, new ParameterizedTypeReference<Recipe>(){});
        Assertions.assertEquals(2, responseAfterDelete.getBody().size());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseForDeletedRecipe.getStatusCode());

    }

    private Ingredient createIngredient(String name, int quantity) {
        return TestUtil.TestIngredientBuilder.newBuilder().withName(name).withQuantity(quantity).build();
    }

    private Recipe createRecipe(String recipeName, int noOfServings, List<Ingredient> ingredients,
                                String instructions, String isVegetarian) {
        return TestUtil.TestRecipeBuilder.newBuilder().withName(recipeName).withServing(noOfServings).withIngredients(ingredients)
                .withInstructions(instructions).isVegetarian(isVegetarian).build();
    }

}


