package io.github.danipenaperez.starter.flowedrouting.context;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

public class FlowedRoutingEnvironmentPostProcessor implements EnvironmentPostProcessor{

	public static String APPLICATION_RUN_BASE_PACKAGE;
	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		
		FlowedRoutingEnvironmentPostProcessor.APPLICATION_RUN_BASE_PACKAGE = application.getMainApplicationClass().getPackageName();
	
	}

}
