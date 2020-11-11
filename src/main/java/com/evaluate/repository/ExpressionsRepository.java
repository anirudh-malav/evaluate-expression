package com.evaluate.repository;

import com.evaluate.model.Expressions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpressionsRepository extends JpaRepository<Expressions, String> {

    Expressions findByUserEmail(String userEmail);
}
