# Spring boot Flowed Routing Simple


## Introduction

This demo show a simple usecase (multitenant approach) , to execute different implementations of the same service based on runtime **input parameters and a context Bean** to execute a SpEl Expression.

The ExecutionContext.java bean will provide access to header value.

# HOW TO RUN

Simply Spring boot run:
```sh
mvn clean install spring-boot:run 
```
# TEST

Invoke the greeting Controller with different parameters:

To get response from AUsersGrettingService use this curl using username that startsWith ('A') and the header with tenant_1:

```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Anthony' --header 'tenantId: tenant_1'
You are amazing Anthony because you owns to tenant_1
```

To get reponse from the Default implementation user not started A name or use different tenant header value

```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Anthony' --header 'tenantId: tenant_2'
Greetings for Anthony

```