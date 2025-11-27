package io.github.danipenaperez.starter.flowedrouting.context;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import io.github.danipenaperez.lib.flowedrouting.annotation.RoutedInterface;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FlowedRoutingClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

	  public FlowedRoutingClassPathBeanDefinitionScanner( BeanDefinitionRegistry registry, Class<? extends Annotation> annotationType) {
	    super(registry, false);
	    addIncludeFilter(new AnnotationTypeFilter(annotationType));
	  }
	  
	  @Override
	  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
	    
		AnnotationMetadata metadata = beanDefinition.getMetadata();
		boolean isCandidate = metadata.isInterface() && !metadata.isAnnotation() && metadata.hasAnnotation(RoutedInterface.class.getTypeName());

	    return isCandidate;
	  }

	  @Override
	  protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
		System.out.println("Y el bean Name es "+beanName);
	    beanDefinition.setBeanClassName(getFactoryBeanClassName()); //Set real class name the FactoryProxyBean

	    beanDefinition
	    	.getConstructorArgumentValues()
	        .addGenericArgumentValue(((AnnotatedBeanDefinition) beanDefinition).getMetadata());

	    beanDefinition.setDependsOn(getFactoryBeanDependencies());
	  }
	  
	  protected String getFactoryBeanClassName() {
	    return FlowedRoutingProxiedInterfaceBean.class.getName();
	  }
	  
	  protected String[] getFactoryBeanDependencies() {
	    return new String[] { FlowedRoutingComponentProxyFactory.class.getTypeName() };
	  }
}
