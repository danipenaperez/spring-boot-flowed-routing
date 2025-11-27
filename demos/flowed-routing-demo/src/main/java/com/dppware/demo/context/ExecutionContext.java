package com.dppware.demo.context;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ExecutionContext {

	public String getTenantName () {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		  // get the request
		HttpServletRequest request = requestAttributes.getRequest();
		return request.getHeader("tenantId");
	}
}
