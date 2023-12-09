package com.assignment.amit.recipemanager.service;

import com.assignment.amit.recipemanager.entity.IngredientEntity;
import com.assignment.amit.recipemanager.entity.RecipeEntity;
import com.assignment.amit.recipemanager.exceptions.RecipeAlreadyExistException;
import com.assignment.amit.recipemanager.exceptions.RecipeNotFoundException;
import com.assignment.amit.recipemanager.util.RecipeUtil;
import com.assignment.amit.recipemanager.model.Recipe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    private final MongoTemplate mongoTemplate;

    /**
     * Adds recipe to the Database
     *
     * @param recipe to be sstored
     * @return
     */
    public Recipe addRecipe(Recipe recipe) {
        RecipeEntity entity = RecipeUtil.mapToRecipeEntity(recipe);
        try {
            return RecipeUtil.maoToRecipe(mongoTemplate.save(entity));
        } catch (DuplicateKeyException ex) {
            throw new RecipeAlreadyExistException("Recipe Already Exist");
        }

    }

    /**
     * Return all recipes from the database
     *
     * @return
     */
    public List<Recipe> getAllRecipes() {
        return mongoTemplate.findAll(RecipeEntity.class).stream().map(RecipeUtil::maoToRecipe).toList();
    }

    /**
     * Return recipe for given recipe name from the database
     *
     * @param recipeName recipe name to be retrieved
     * @return Recipe Object
     */
    public Recipe getRecipe(String recipeName) {
        RecipeEntity recipeEntityReturned = mongoTemplate.findOne(createMongoQuery(Map.of("recipeName", recipeName)), RecipeEntity.class);
        if (Objects.isNull(recipeEntityReturned)) {
            throw new RecipeNotFoundException("Recipe Not available");
        }
        return RecipeUtil.maoToRecipe(recipeEntityReturned);
    }

    /**
     * Deletes recipe from the database.
     *
     * @param recipeName recipe name to be deleted.
     */
    public void deleteRecipe(String recipeName) {
        RecipeEntity recipeEntityReturned = mongoTemplate.findOne(createMongoQuery(Map.of("recipeName", recipeName)), RecipeEntity.class);
        if (Objects.isNull(recipeEntityReturned)) {
            throw new RecipeNotFoundException("Recipe Not available");
        }
        mongoTemplate.remove(recipeEntityReturned);
        mongoTemplate.remove(recipeEntityReturned);
        log.debug("Recipe with id={} deleted", recipeName);
    }

    /**
     * Retrieves the recipes based on the criteria filter passed in.
     *
     * @param filterMap map of key value containing filter criteria.
     * @return List of recipes matching the criteria
     */
    public List<Recipe> getAllRecipesWithFiler(Map<String, String> filterMap) {
        List<RecipeEntity> saveRecipes = mongoTemplate.find(createMongoQuery(filterMap), RecipeEntity.class);
        return saveRecipes.stream().map(RecipeUtil::maoToRecipe).toList();
    }

    /**
     * Creates mongo criteria based on key value passed in.
     */
    private Criteria getCriteria(String key, String value) {
        if (value.matches("[0-9]")) {
            return Criteria.where(key).is(Integer.parseInt(value));
        }
        return value.startsWith("!") ? Criteria.where(key).ne(value.replace("!", "")) : Criteria.where(key).is(value);
    }

    /**
     * Create mongo query with criteria passed in
     *
     * @param filterMap criteria filter map
     * @return Mongo query
     */
    private Query createMongoQuery(Map<String, String> filterMap) {
        final List<Criteria> criteriaList = filterMap.entrySet().stream().map(entry -> {
            if (entry.getKey().equals("instructions")) {
                return Criteria.where("instructions").regex(entry.getValue());
            }
            if (entry.getKey().contains("ingredient")) {
                return getCriteria("ingredients.name", entry.getValue());
            }
            return getCriteria(entry.getKey(), entry.getValue());
        }).toList();
        if (criteriaList.isEmpty()) {
            return new Query(new Criteria());
        }
        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        return new Query(criteria);
    }

    /**
     * Update the recipe which match the criteria with passed name and update it with new values
     *
     * @param recipeName     Name of the recipe being updated
     * @param receivedRecipe recipeObject containing values to be updated
     * @return updated recipe object
     */
    public Recipe updateRecipe(String recipeName, Recipe receivedRecipe) {
        RecipeEntity savedEntity = mongoTemplate.findOne(createMongoQuery(Map.of("recipeName", recipeName)), RecipeEntity.class);
        if (Objects.isNull(savedEntity)) {
            throw new RecipeNotFoundException("Recipe Not available");
        }
        updateRecipe(savedEntity, receivedRecipe);
        return RecipeUtil.maoToRecipe(mongoTemplate.save(savedEntity));
    }

    /**
     * Helper method to updated exiting recipe with new values.
     */
    private void updateRecipe(RecipeEntity exiting, Recipe received) {
        if (isNotNullOrNotEmpty(received.recipeName())) {
            exiting.setRecipeName(received.recipeName());
        }
        if (received.servings() > 0) {
            exiting.setServings(received.servings());
        }
        if (isNotNullOrNotEmpty(received.isVegetarian())) {
            exiting.setIsVegetarian(received.isVegetarian());
        }
        if (isNotNullOrNotEmpty(received.instructions())) {
            exiting.setInstructions(received.instructions());
        }
        if (Objects.nonNull(received.ingredients())) {
            List<IngredientEntity> ingredientList = RecipeUtil.mapToIngredientsEntity(received.ingredients());
            exiting.setIngredients(ingredientList);
        }
    }

    private boolean isNotNullOrNotEmpty(String value) {
        return (Objects.nonNull(value) && !value.isBlank());
    }
}
