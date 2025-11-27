package io.github.danipenaperez.starter.flowedrouting.context;

import static org.springframework.util.ClassUtils.resolveClassName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import io.github.danipenaperez.lib.flowedrouting.evaluator.Evaluator;
import io.github.danipenaperez.lib.flowedrouting.evaluator.annotation.EvaluatorType;
import io.github.danipenaperez.lib.flowedrouting.exception.FlowedRoutingConfigurationException;
import io.github.danipenaperez.lib.flowedrouting.interceptor.DefaultFlowRoutingInterceptor;
import io.github.danipenaperez.lib.flowedrouting.interceptor.FlowRoutingInterceptor;

/**
 * Factory for all @RoutedInterface interfaces
 */
@Component
public class FlowedRoutingComponentProxyFactory implements ApplicationListener<ApplicationStartedEvent>{
	
	  private ApplicationContext context;
	  private List<FlowRoutingInterceptor> interceptorInstances = new ArrayList<>();
	  
	  public FlowedRoutingComponentProxyFactory( ApplicationContext context ) {
		  this.context=context;
	  }

  	  /**
  	   * Create a proxy implementation for a bean based on Bean Class
  	   * @param annotationMetadata
  	   * @return
  	   */
	  public Object createProxy(AnnotationMetadata annotationMetadata) {
	    Class clazz = resolveClassName(annotationMetadata.getClassName(), null);
	   
	    var interceptorInstance = new DefaultFlowRoutingInterceptor(clazz);
	    interceptorInstances.add(interceptorInstance);
	    return ProxyFactory.getProxy(clazz, interceptorInstance);
	  }
	  /**
	   * Populate Interceptors with useful context beans, such as bean implementations, and Evaluators
	   */
	  @Override
	  public void onApplicationEvent(ApplicationStartedEvent event) {
		  try {
			  registerEvaluatorsToInterceptors();
			  registerDelegatesToInterceptors();
		  }catch (FlowedRoutingConfigurationException exc){
			  throw new BeanInitializationException(exc.getMessage());
		  }
	  }
	  
	  private void registerDelegatesToInterceptors() throws FlowedRoutingConfigurationException {
		 for(FlowRoutingInterceptor interceptorInstance: interceptorInstances) {
			 Map beanDelegates = context.getBeansOfType(interceptorInstance.getWrappedInterfaceClass());
			 if(beanDelegates == null || beanDelegates.size()==0)
				 throw new FlowedRoutingConfigurationException("Not found @RoutedComponent beans that implements the @RoutedInterface "+interceptorInstance.getClass().getName());
			 interceptorInstance.addDelegates(beanDelegates.values());
		 }

	  }
	  
	  private void registerEvaluatorsToInterceptors() throws FlowedRoutingConfigurationException {
		  interceptorInstances.forEach(interceptor->{ //context.getBean("SpELEvaluator")
			  Map evaluators = context.getBeansWithAnnotation(EvaluatorType.class);
			  for(Object evaluator:evaluators.values()) {
				  interceptor.registerEvaluator((Evaluator) evaluator);
			  }
		  });
	  }

	    
}
