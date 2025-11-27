# lib-utils-json

## About The Project

Java Utilities to filtering and manage JSON Objects.

Given a JSONObject you can exclude or leave only the items across the whole object.

## Installation

Add the maven dependency:

```xml
  <dependency>
   	<groupId>io.github.danipenaperez</groupId>
	   <artifactId>lib-utils-json</artifactId>
	   <version>0.1.0</version>
  </dependency>
```

## Usage examples

Given this Json (via JSONObject):

```java
String data = """
{
   "countries":[
      {
         "name":"Spain",
         "habitants":48619695,
         "cities":[
            {
               "name":"Madrid",
               "flagImage": "madrid.jpeg",
               "location":{
                  "latitude":40.4165,
                  "longitude":-3.70256
               }
            },
            {
               "name":"Barcelona",
               "flagImage": "barcelona.jpeg",
               "location":{
                  "latitude":41.38879,
                  "longitude":2.15899
               }
            }
         ]
      },
      {
         "name":"France",
         "habitants":68401997,
         "cities":[
            {
               "name":"Paris",
               "flagImage": "paris.jpeg",
               "location":{
                  "latitude":48.85341,
                  "longitude":2.3488
               }
            }
         ]
      }
   ],
   "pagination":{
      "requestedPage":1,
      "pageSize":2
   }
}
""";
```

### Include filtering

You only want to maintain this fields: 

- countries_name: will maintain name in all countries array objects. If countries were and object, "name" will be included.   
- countries_cities_name :  same behaviour
- countries_cities_location_latitude : all other not specified nodes at location longitude will not be included (so longitude will not be included)
- pagination_* : will maintain all nodes inside pagination object


usage of **FilterInclude.java**: 

```java

String includeNodes = "pagination_*, countries_name, countries_cities_name, countries_cities_location_latitude ";
		
JSONObject jsonObject =new FilterInclude().apply(new JSONObject(data), includeNodes);

System.out.println(jsonObject); 

```

will print :

```json

{
   "pagination":{
      "requestedPage":1,
      "pageSize":2
   },
   "countries":[
      {
         "cities":[
            {
               "name":"Madrid",
               "location":{
                  "latitude":40.4165
               }
            },
            {
               "name":"Barcelona",
               "location":{
                  "latitude":41.38879
               }
            }
         ],
         "name":"Spain"
      },
      {
         "cities":[
            {
               "name":"Paris",
               "location":{
                  "latitude":48.85341
               }
            }
         ],
         "name":"France"
      }
   ]
}

```

### Exclude filtering

Imagine you only want to specified to exclude certain nodes:

- pagination* : does not include the pagination object itself
- countries_habitants: if countries were and object will delete "habitants" node. If were an array, will delete "habitants" from all childs.
- countries_cities_flagImage 
- countries_cities_location:  only leave the other nodes



usage of **FilterExclude.java**: 

```java

String includeNodes = "pagination*, countries_habitants, countries_cities_flagImage, countries_cities_location_latitude";
		
JSONObject jsonObject =new FilterExclude().apply(new JSONObject(data), includeNodes);

System.out.println(jsonObject); 

```

output:

```json
{
   "countries":[
      {
         "cities":[
            {
               "name":"Madrid",
               "location":{
                  "longitude":-3.70256
               }
            },
            {
               "name":"Barcelona",
               "location":{
                  "longitude":2.15899
               }
            }
         ],
         "name":"Spain"
      },
      {
         "cities":[
            {
               "name":"Paris",
               "location":{
                  "longitude":2.3488
               }
            }
         ],
         "name":"France"
      }
   ]
}

```



## Advanced Recursive

- If you want to include/exclude all nodes inside and object use  ( object_child_coordinates_* ) 

- If want to include/exclude the whole tree childs of an object use  ( object_child_coordinates\* )   (note underscore)





### LOCAL DEPLOY TO MAVEN CENTRAL

Use Profile ci-cd

At my local computer:

```sh
mvn clean deploy -P ci-cd
```


<!-- LICENSE -->
## License

Distributed under the Unlicense License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



