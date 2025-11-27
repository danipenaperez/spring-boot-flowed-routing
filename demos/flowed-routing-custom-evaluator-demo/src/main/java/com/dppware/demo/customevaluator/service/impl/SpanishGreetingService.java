package com.dppware.demo.customevaluator.service.impl;

import com.dppware.demo.customevaluator.evaluator.FlowWeatherCondition;
import com.dppware.demo.customevaluator.service.GreetingService;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedComponent;
import io.github.danipenaperez.lib.flowedrouting.condition.FlowConditionType;

@RoutedComponent
public class SpanishGreetingService  implements GreetingService{

	@FlowConditionType("wheather")
	@FlowWeatherCondition(wheaterCondition = "raining")
	@Override
	public String greeting(String language){
		return "it is raining";
	}

}
