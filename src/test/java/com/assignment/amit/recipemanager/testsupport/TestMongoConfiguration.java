package com.assignment.amit.recipemanager.testsupport;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestMongoConfiguration {
    @Value("${spring.test.mongo.docker.image}")
    private String mongoDbImage;

    @Bean("testMongoContainer")
    public MongoDBContainer getContainer(){
        final MongoDBContainer container =
                new MongoDBContainer((DockerImageName.parse(mongoDbImage).asCompatibleSubstituteFor("mongo:4.0.10")));
        container.start();
        return container;
    }

    @Primary
    @Bean
    @DependsOn("testMongoContainer")
    public MongoClient testMongoClient(final @Qualifier("testMongoContainer") MongoDBContainer container){
        return MongoClients.create(container.getReplicaSetUrl("testdatabase"));
    }
}
