package com.dppware.demo.customevaluator.evaluator;

import java.util.Random;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import io.github.danipenaperez.lib.flowedrouting.definition.RoutedMethodDefinition;
import io.github.danipenaperez.lib.flowedrouting.evaluator.Evaluator;
import io.github.danipenaperez.lib.flowedrouting.evaluator.annotation.EvaluatorType;
import lombok.extern.slf4j.Slf4j;

@Component
@EvaluatorType(value = "wheather")
@Slf4j
public class WheatherEvaluator implements Evaluator{

	Random random = new Random();
	
	@Override
	public boolean evaluate(RoutedMethodDefinition routedMethodDefinition, MethodInvocation invocation) {
		log.info("Using Wheather evaluator");
		FlowWeatherCondition flowWeatherCondition = routedMethodDefinition.getMethod().getDeclaredAnnotation(FlowWeatherCondition.class);
		if(getCurrentWeather().equals(flowWeatherCondition.wheaterCondition())){
			return true;
		}else {
			return false;
		}
	}
	
	public String getCurrentWeather() {
		
	    if( random.nextBoolean()) {
	    	return "sunny";
	    }else {
	    	return "raining";
	    }
	}

}
