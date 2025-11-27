package com.dppware.demo.service;

import org.springframework.stereotype.Component;

import com.dppware.demo.database.repository.FlagRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class FlagService {
	
	FlagRepository flagRepository;
	
	public boolean isFlagActive(String flagName) {
		return flagRepository.isFlagEnabled(flagName);
	}
	

}
