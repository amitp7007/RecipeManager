package com.assignment.amit.recipemanager.controller;

import com.assignment.amit.recipemanager.exceptions.RecipeAlreadyExistException;
import com.assignment.amit.recipemanager.exceptions.RecipeNotFoundException;
import com.assignment.amit.recipemanager.model.Recipe;
import com.assignment.amit.recipemanager.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@Slf4j
public class RecipeController {
    private final RecipeService recipeService;

    /**
     * Api to handles the recipe create request from user
     *
     * @param recipe recipe to be created
     * @return ResponseEntity<Recipe>
     */
    @PostMapping()
    public ResponseEntity<Recipe> addRecipe(@RequestBody Recipe recipe) {
        log.debug("Request received to add the recipe.");
        try {
            return new ResponseEntity<Recipe>(recipeService.addRecipe(recipe), HttpStatus.CREATED);
        } catch (RecipeAlreadyExistException ex) {
            log.error("Error While processing request ", ex);
            throw ex;
        } catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }
    }

    /**
     * Api to handle request from user to search specific recipe based on filter
     *
     * @param filterMap criteria filter
     * @return ResponseEntity<Recipe>
     */
    @GetMapping
    public List<Recipe> getRecipesByFilter(@RequestParam Map<String, String> filterMap) {
        log.debug("Request received to fetch all the recipes by filter criteria");
        try {
            return recipeService.getAllRecipesWithFiler(filterMap);
        } catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }

    }

    /**
     * Api to handle request to retrieve a recipe.
     *
     * @param recipeName recipeName
     * @return ResponseEntity<Recipe>
     */
    @GetMapping("/{recipeName}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable String recipeName) {
        log.debug("Requesting for recipe name={}", recipeName);
        try {
            return ResponseEntity.ok(recipeService.getRecipe(recipeName));
        } catch (RecipeNotFoundException recipeNotFound) {
            log.error("Recipe Not found in the system with recipeName = {}", recipeName);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe Not Found");
        } catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }
    }

    /**
     * Api to handle the updated request for specific recipe.
     *
     * @param recipeName recipe name to be updated
     * @param recipe     recipe object containing field to be updated
     * @return updated recipe
     */
    @PutMapping("/{recipeName}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String recipeName, @RequestBody Recipe recipe) {
        log.debug("Requesting update for recipe name={}", recipeName);
        try {
            return ResponseEntity.ok(recipeService.updateRecipe(recipeName, recipe));
        } catch (RecipeNotFoundException recipeNotFound) {
            log.error("Recipe Not found in the system with name = {}", recipeName);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe Not Found");
        } catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }
    }

    /**
     * Api to delete recipe
     *
     * @param recipeName name of recipe
     * @return ResponseEntity<Recipe>
     */
    @DeleteMapping("/{recipeName}")
    public ResponseEntity<Recipe> deleteRecipe(@PathVariable String recipeName) {
        log.debug("Requesting for recipe id={}", recipeName);
        try {
            recipeService.deleteRecipe(recipeName);
        } catch (RecipeNotFoundException recipeNotFound) {
            log.error("Recipe Not found in the system with id = {}", recipeName);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe Not Found");
        } catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
