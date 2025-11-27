# Spring boot Flowed Routing Simple


## Introduction

This demo show a feature Flags service integration with [OpenFeature](https://openfeature.dev/) on opensource [https://gofeatureflag.org/](https://gofeatureflag.org/) 

# REQUISITES

## 1.Starting GOFeatureFlags docker local service:

## 1.1 Create the configuration that contains the flags (variants and Rules).

For example create a simple flag to enable o disable feature for a determinated tenant. As shown only "tenant_1" has available the **new-greeting-feature** :

```sh
new-greeting-flag:
  variations:
    available-new-greeting-feature: true
    not-available-new-greeting-feature: false
  targeting:
    - name: Enabled Tenants Evaluation Rule
      query: tenantId eq "tenant_1"
      variation: available-new-greeting-feature
  defaultRule:
    variation: not-available-new-greeting-feature
```
 
 You can create or edit your flags using online editor at [https://gofeatureflag.org/editor](https://gofeatureflag.org/editor)
  
## 1.2 Create the configuration that contains the flags (variants and Rules).  

Create the configuration file to use local file flag store:

```sh
retrievers:
  - kind: file
    path: /goff/flag-config.goff.yaml # Location of the flags configuration file in your docker container.
```

## 1.3 Run the server

```sh

cd docker

docker run --rm \
  -p 1031:1031 \
  -v $(pwd)/flag-config.goff.yaml:/goff/flag-config.goff.yaml \
  -v $(pwd)/goff-proxy.yaml:/goff/goff-proxy.yaml \
  gofeatureflag/go-feature-flag:latest
```  

# 2.Create the client and asociate with @RoutedComponent

Create a simple clien to fetch goFeatureFlag local server api:

```java
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

```

Reference the client at @FlowSpelCondition:

	@FlowSpelCondition(evaluationExpression = "@goFeatureFlagsClient.isFlagActiveForCurrentRequest('new-greeting-flag')")

```java
@RoutedComponent
public class AUsersGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = "@goFeatureFlagsClient.isFlagActiveForCurrentRequest('new-greeting-flag')") 
	@Override
	public String greeting(String userName){
		return "You are amazing "+userName +" because new-greeting-flag is active";
	}
}
```



# 3. Run the demo

Simply Spring boot run:

```sh
mvn clean install spring-boot:run 
```
# 4. TEST

Only users that tenant_1 will execute the new greeting Service:

```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Anthony' --header 'tenantId: tenant_1'

You are amazing Anthony because new-greeting-flag is active
```

Other users will get the default executions:

```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Anthony' --header 'tenantId: tenant_2'

Greetings for Anthony
```

# 4. Benefics

Using openFeature server implementation services will managed dymanic the up / down flags.
