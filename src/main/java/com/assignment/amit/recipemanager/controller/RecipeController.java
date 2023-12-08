package com.assignment.amit.recipemanager.controller;

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
     * @param recipe
     * @return
     */
    @PostMapping
    public Recipe addRecipe(@RequestBody Recipe recipe) {
        log.debug("Request received to fetch all the recipes");
        try {
            return recipeService.addRecipe(recipe);
        } catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }
    }

    /**
     * @param filterMap
     * @return
     */
    @GetMapping
    public List<Recipe> getRecipesByFilter(@RequestParam Map<String, String> filterMap) {
        log.debug("Request received to fetch all the recipes by filter criteria");
        try {
            return recipeService.getAllRecipesWithFiler(filterMap);
        }catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }

    }
//    /**
//     * @return
//     */
//    @GetMapping
//    public List<Recipe> getRecipes() {
//        log.debug("Request received to fetch all the recipes by filter criteria");
//        try {
//            return recipeService.getAllRecipes();
//        }catch (RuntimeException ex) {
//            log.error("Error While processing request ", ex);
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
//        }
//
//    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable String id) {
        log.debug("Requesting for recipe id={}", id);
        try {
            return ResponseEntity.ok(recipeService.getRecipeById(id));
        } catch (RecipeNotFoundException recipeNotFound) {
            log.error("Recipe Not found in the system with id = {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foo Not Found");
        } catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Recipe> deleteRecipe(@PathVariable String id) {
        log.debug("Requesting for recipe id={}", id);
        try {
            recipeService.deleteRecipeById(id);
        } catch (RecipeNotFoundException recipeNotFound) {
            log.error("Recipe Not found in the system with id = {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foo Not Found");
        } catch (RuntimeException ex) {
            log.error("Error While processing request ", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Error");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
