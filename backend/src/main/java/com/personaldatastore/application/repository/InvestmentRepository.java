package com.personaldatastore.application.repository;

import com.personaldatastore.application.domain.Investment;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface InvestmentRepository extends Neo4jRepository<Investment, Long> {
}
