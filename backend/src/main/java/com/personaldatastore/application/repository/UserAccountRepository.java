package com.personaldatastore.application.repository;

import com.personaldatastore.application.domain.UserAccount;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;

public interface UserAccountRepository extends Neo4jRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
}
