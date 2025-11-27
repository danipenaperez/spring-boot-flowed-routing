# Spring boot Flowed Routing Simple


## Introduction

This demo show a simple usecase , to execute different implementations of the same service based on runtime input parameters and a simple SpEl Expression



# HOW TO RUN

Simply Spring boot run:
```sh
mvn clean install spring-boot:run 
```
# TEST

Invoke the greeting Controller with different parameters:

To get response from AUsersGrettingService use this curl using username that startsWith ('A') and owns to tenant_1:

```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Anthony'
You are amazing Anthony
```

To get reponse from the Default implementation user not started A name

```sh
curl --location --request GET 'http://localhost:8080/greeting?userName=Daniel'
Greetings for Daniel

```