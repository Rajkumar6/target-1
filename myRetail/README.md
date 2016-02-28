= My Retail API Service =   
This projects contains the implementation of the My Retail API service

== Technologies ==
 1. Mule ESB
   * Mule handles the request, interacts with the backend Target Rest API and send back the response
 1. MongoDB
 1. Maven
 1. Java

== Setup ==
1. Have a Mongo DB instance available
1. Configure the service to use the database

== How to Compile ==
mvn clean package

== How to Run ==
mvn clean deploy
server is running on http://localhost:8080/myRetail/api/v1.0/products/{id}
