package io.github.danipenaperez.starter.flowedrouting.context;

import static org.springframework.util.ClassUtils.resolveClassName;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.type.AnnotationMetadata;

public class FlowedRoutingProxiedInterfaceBean implements FactoryBean<Object>, BeanFactoryAware {
	private final AnnotationMetadata metadata;
	private final Class<?> objectType;
	private FlowedRoutingComponentProxyFactory flowedRoutingComponentProxyFactory;
	
	public FlowedRoutingProxiedInterfaceBean(AnnotationMetadata metadata) {
		this.metadata = metadata;
		objectType = resolveClassName(metadata.getClassName(), null);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		this.flowedRoutingComponentProxyFactory = (FlowedRoutingComponentProxyFactory) beanFactory.getBean(FlowedRoutingComponentProxyFactory.class.getTypeName());
	}

	/**
	 * Returns the instance (singleton) that will receive real invocations.
	 * In this case, returns a MethodInterceptor for the Bean original class
	 */
	@Override
	public Object getObject() {
		return flowedRoutingComponentProxyFactory.createProxy(metadata);
	}

	@Override
	public Class<?> getObjectType() {
		return objectType;
	}


}
