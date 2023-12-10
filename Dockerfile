FROM eclipse-temurin:21-jre-alpine
COPY target/recipemanager-0.0.1-SNAPSHOT.jar recipemanager.jar
ENTRYPOINT ["java","-jar","/recipemanager.jar"]