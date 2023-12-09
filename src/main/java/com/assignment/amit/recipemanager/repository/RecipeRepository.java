package com.assignment.amit.recipemanager.repository;

import com.assignment.amit.recipemanager.entity.RecipeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecipeRepository extends MongoRepository<RecipeEntity, String>{
    public Optional<RecipeEntity> findByRecipeName(String recipeName);
}
