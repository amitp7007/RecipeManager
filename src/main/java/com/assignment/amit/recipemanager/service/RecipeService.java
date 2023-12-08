package com.assignment.amit.recipemanager.service;

import com.assignment.amit.recipemanager.entity.RecipeEntity;
import com.assignment.amit.recipemanager.exceptions.RecipeNotFoundException;
import com.assignment.amit.recipemanager.util.RecipeUtil;
import com.assignment.amit.recipemanager.model.Recipe;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.assignment.amit.recipemanager.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {
	private final RecipeRepository recipeRepo;
	private final MongoTemplate mongoTemplate;
	public static final String[] SIMPLE_OPERATION_SET = { ":", "!", ">", "<", "~" };

	/**
	 * Adds recipe to the Database
	 * @param recipe to be sstored
	 * @return
	 */
	public Recipe addRecipe(Recipe recipe) {
		RecipeEntity entity = RecipeUtil.mapToRecipeEntity(recipe);
		return RecipeUtil.maoToRecipe(recipeRepo.save(entity));
	}

	/**
	 *
	 * @return
	 */
	public List<Recipe> getAllRecipes() {
		return recipeRepo.findAll().stream().map(RecipeUtil::maoToRecipe).toList();
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public Recipe getRecipeById(String id) {
		Optional<RecipeEntity> recipe = recipeRepo.findById(id);
		if(recipe.isEmpty()){
			throw new RecipeNotFoundException("Recipe Not available");
		}
		return RecipeUtil.maoToRecipe(recipe.get());
	}

	/**
	 *
	 * @param id
	 */
	public void deleteRecipeById(String id) {
			if(recipeRepo.findById(id).isEmpty())
				throw new RecipeNotFoundException("Recipe Not available");
			recipeRepo.deleteById(id);

	}

	public List<Recipe> getAllRecipesWithFiler(Map<String, String> filterMap) {
		List<RecipeEntity> saveRecipes = mongoTemplate.find(createMongoCriteriaQuery(filterMap), RecipeEntity.class);
		return saveRecipes.stream().map(RecipeUtil::maoToRecipe).toList();
	}

	private Query createMongoCriteriaQuery(Map<String, String> filterMap){
		final List<Criteria> criteriaList = filterMap.entrySet().stream().map(entry -> {
			if(entry.getKey().equals("search")){
				return Criteria.where("instructions").regex(entry.getValue());
			}
			if(entry.getKey().contains("ingredient")){
				return Criteria.where("ingredients.name").is(entry.getValue());
			}
			return Criteria.where(entry.getKey()).is(entry.getValue());
		}).toList();
		if(criteriaList.isEmpty()){
			Criteria c = new Criteria();
			return new Query(new Criteria());
		}
		Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
		return new Query(criteria);
	}
}
