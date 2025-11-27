package io.github.danipenaperez.starter.flowedrouting.context;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

@Configuration
public class FlowedRoutingBeanRegistrar implements ImportBeanDefinitionRegistrar{

    @Override
    public void registerBeanDefinitions (AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    	FlowedRoutingClassPathBeanDefinitionScanner scanner = new FlowedRoutingClassPathBeanDefinitionScanner(registry, Component.class);
    	scanner.scan(FlowedRoutingEnvironmentPostProcessor.APPLICATION_RUN_BASE_PACKAGE);
    }


}
