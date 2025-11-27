package com.dppware.demo.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.dppware.demo.database.model.Flag;

@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> {
	
	@Query("SELECT f.enabled FROM Flag f WHERE f.feature=?1")
    Boolean isFlagEnabled(String flagName);
	
}

