# Spring boot Flowed Routing Simple


## Introduction

This demo show a simple usecase based on database H2 flaged feature


| feature          | enabled |
|------------------|---------|
| greeting_new_service | true   |
|                  |         |

## Code

1. Create the repository with the query:
```java
@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> {
	
	@Query("SELECT f.enabled FROM Flag f WHERE f.feature=?1")
    Boolean isFlagEnabled(String flagName);
	
}
```

2. Create the Component that uses the repository

```java
@Component
@AllArgsConstructor
public class FlagService {
	FlagRepository flagRepository;
	
	public boolean isFlagActive(String flagName) {
		return flagRepository.isFlagEnabled(flagName);
	}
}
```

3. Enable or disable your RoutedComponent based on database flag state 

```java
@RoutedComponent
public class NewGreetingService  implements GreetingService{

	@FlowConditionType("SpEL") //Indicate use default evaluator provided in starter. Will use SpEL expressions
	@FlowSpelCondition(evaluationExpression = "@flagService.isFlagActive('greeting_new_service')") 
	@Override
	public String greeting(String userName){
		return "You are amazing "+userName +" because you are using the NEW Greeting Service";
	}
}
```

# HOW TO RUN

Simply Spring boot run:
```sh
mvn clean install spring-boot:run 
```
# DATABASE H2 

Is available at [http://localhost:8080/h2-console](http://localhost:8080/h2-console)

The connection jdbc url: jdbc:h2:mem:test

![docs/h2-console-entry.png](docs/h2-console-entry.png)

A table falled Flag will be created with the flag configured

![docs/h2_flag_table.png](docs/h2_flag_table.png)



# TEST

The execution of NewGreetingService is available because the flag is set to true:

```java

  @FlowConditionType("SpEL") SpEL expressions
	@FlowSpelCondition(evaluationExpression = "@flagService.isFlagActive('greeting_new_service')") 
	@Override
	public String greeting(String userName){
		return "You are amazing "+userName +" because you are using the NEW Greeting Service";
	}

```


Invoke the greeting Controller with some data and the NewService will do the execution


```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Anthony'
You are amazing Anthony because you are using the NEW Greeting Service
```

Update the flag value to false using H2 web console

```sh
UPDATE FLAG SET enabled = false WHERE feature = 'greeting_new_service';

```

Retry the same request, and will be used de DefaultGreeting Service:


```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Anthony'
Greetings for Anthony

```