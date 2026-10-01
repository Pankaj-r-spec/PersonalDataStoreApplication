package com.personaldatastore.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@Configuration
@EnableNeo4jRepositories(basePackages = "com.personaldatastore.application.repository")
public class Neo4jConfig {
}
