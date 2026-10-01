package com.personaldatastore.application.repository;

import com.personaldatastore.application.domain.Expense;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ExpenseRepository extends Neo4jRepository<Expense, Long> {
}
