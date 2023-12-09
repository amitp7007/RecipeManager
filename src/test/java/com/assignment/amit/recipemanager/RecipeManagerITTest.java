package com.assignment.amit.recipemanager;

import com.assignment.amit.recipemanager.model.Ingredient;
import com.assignment.amit.recipemanager.model.Recipe;
import com.assignment.amit.recipemanager.testsupport.TestMongoConfiguration;
import com.assignment.amit.recipemanager.testsupport.TestUtil;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RecipeManagerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("integration")
@Import(TestMongoConfiguration.class)
public class RecipeManagerITTest {
    @LocalServerPort
    private int port;
    @Autowired
    private MongoTemplate mongoTemplate;
    String createUrl(String path){
        return  "http://localhost:"+ port + path;
    }

    @AfterEach
    public void cleanup(){
        mongoTemplate.dropCollection(ResponseEntity.class);
    }
    @Test
    public void testGetRecipe() throws JSONException {
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
    public void testAddRecipe() throws JSONException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        //
        List<Ingredient> ingredient = List.of(TestUtil.TestIngredientBuilder.newBuilder().withName("Coriander").withQuantity(2).build());
        Recipe recipe = TestUtil.TestRecipeBuilder.newBuilder().withName("PaneerLababdar").withIngredients(ingredient)
                .withServing(5).withInstructions("Start with frying the paneer").build();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        TestRestTemplate restTemplate = new TestRestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(recipe), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createUrl("/recipes"),
                HttpMethod.POST, entity, String.class);

        Assertions.assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        }
}
