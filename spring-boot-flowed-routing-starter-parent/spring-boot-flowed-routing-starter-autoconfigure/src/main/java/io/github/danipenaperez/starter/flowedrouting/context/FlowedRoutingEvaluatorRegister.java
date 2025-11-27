package io.github.danipenaperez.starter.flowedrouting.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.danipenaperez.lib.flowedrouting.spel.SpELEvaluator;
/**
 * Register Evaluators at context
 */
@Configuration
public class FlowedRoutingEvaluatorRegister {

	@Bean
	public SpELEvaluator SpELEvaluator(ApplicationContext context) {
		return new SpELEvaluator(context);
	}
	
}
