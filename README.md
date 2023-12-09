# RecipeManager
This Recipe Manager application allow user to manage thier recipes. User can create recipes with specific ingredients, update the existing recipes and list all and with specific condition.

## Feature
1. Add and Remove Recipes
2. List all the recipes
3. Filter out specific recipes based on ingredients, service, type like veg or non veg

## Tech Stack:
1. Spring Boot: 3+
2. Java : 21
3. MongoDb

## Apis Exposed 
1. Create Recipe:
   ```        
    curl --location 'localhost:8080/recipes' \
    --header 'Content-Type: application/json' \
    --data '{

        "recipeName": "TestRecipe",
        "ingredients": [
            {
                "name": "Coriander1",
                "quantity": 2
            },
            {
                "name": "Termeric",
                "quantity": "2"
            }
        ],
        "servings": 4,
        "instructions": 5,
        "isVegetarian": "true"
    }
    ``` 
2. List All Recipe:
   ``` 
   curl --location 'localhost:8080/recipes'
   
   ```
3. List With Specific filter
   ```
   curl --location 'localhost:8080/recipes?servings=5&ingredient=Coriander1'

   ```
4. Updated specific Recipe
   Example to update servings to 10 for recipe `ChickenTikka5Person3`

   ```
   curl --location --request PUT 'localhost:8080/recipes/ChickenTikka5Person3' \
      --header 'Content-Type: application/json' \
      
   --data '{      
      "servings": 10
      }'
   ```
 
## Build Application
   Go to the project directory
   ```
    mvn clean install
   ```
## Run Application
   Go to the project directory
   ```
    java -jar target/recipemanager-0.0.1-SNAPSHOT.jar
   ```