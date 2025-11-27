package com.dppware.demo.gofeatureflags;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dev.openfeature.contrib.providers.gofeatureflag.GoFeatureFlagProvider;
import dev.openfeature.contrib.providers.gofeatureflag.GoFeatureFlagProviderOptions;
import dev.openfeature.contrib.providers.gofeatureflag.exception.InvalidOptions;
import dev.openfeature.sdk.Client;
import dev.openfeature.sdk.EvaluationContext;
import dev.openfeature.sdk.FeatureProvider;
import dev.openfeature.sdk.FlagEvaluationDetails;
import dev.openfeature.sdk.ImmutableContext;
import dev.openfeature.sdk.MutableContext;
import dev.openfeature.sdk.OpenFeatureAPI;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class GoFeatureFlagsClient {

	Client client;
			
	public GoFeatureFlagsClient() throws InvalidOptions {
		FeatureProvider provider = new GoFeatureFlagProvider(GoFeatureFlagProviderOptions.builder().endpoint("http://localhost:1031").build());
		OpenFeatureAPI.getInstance().setProviderAndWait(provider);
		client = OpenFeatureAPI.getInstance().getClient("flowed-routing-gofeatureflags-demo");
	}
	
	public boolean isFlagActiveForCurrentRequest(String flagName) {
		//Obtain current context
		
		EvaluationContext evaluationContext = new MutableContext(UUID.randomUUID().toString()).add("tenantId", getTenantName());
		
		return client.getBooleanValue("new-greeting-flag", false, evaluationContext);
		
	}
	
	
	public String getTenantName () {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String tenantId = request.getHeader("tenantId")!=null?request.getHeader("tenantId"):"";
		return tenantId;
	}
}
