package com.son.SpringBatch.repository;

import com.son.SpringBatch.domain.One;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OneRepository extends JpaRepository<One, Long> {

    @Override
    @EntityGraph(attributePaths = {"manyList"})
    List<One> findAll();

}
