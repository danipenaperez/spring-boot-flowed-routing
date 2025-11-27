package io.github.danipenaperez.lib.flowedrouting.utils;

import org.springframework.util.ClassUtils;

public class TypeUtils {

	/**
	 * Detect that is object instance is a proxy
	 * @param obj
	 * @return
	 */
	public static boolean isProxy(Object obj) {
		boolean isProxy = false;
		for (Class _interface: obj.getClass().getInterfaces()) {
			if(_interface.equals(org.springframework.aop.SpringProxy.class)||
			_interface.equals(org.springframework.aop.framework.Advised.class)||
			_interface.equals(org.springframework.core.DecoratingProxy.class)||
			obj.getClass().getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
				isProxy=true;
			}
		}
		return isProxy;
	}
}
