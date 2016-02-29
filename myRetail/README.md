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
1. Run mvn clean package

=== Setup MongoDB ===
==== Via Docker ====
1. Start the Database
 ```bash
 $ docker run --name myRetail-mongo -v /data/db:/data/db -d mongo:tag --auth
 ```
1. Add the Initial Admin User
 ```bash
 $ docker exec -it myRetail-mongo mongo admin
 > db.createUser({ user: 'admin', pwd: 'adminpasswd', roles: [ { role: "userAdminAnyDatabase", db: "admin" } ] });
 ```
1. Add the Initial MyRetail User
 ```bash
 $ docker exec -it myRetail-mongo mongo admin
 > db.createUser({ user: 'myRetail', pwd: 'myRetail', roles: [ { role: "userAdminAnyDatabase", db: "admin" } ] });
 ```
==== Locally ====
1. apt-get install mongodb
1. sudo service mongodb start
1. mongo 

=== Create database ===
1. use myRetail
1. db.pricing.insert({"_id":13860428,"current_price":{"value": 13.49,"currency_code":"USD"}});

== How to Compile ==
mvn clean package

== How to Run ==
mvn clean deploy
server is running on http://127.0.0.1:8081/api/products
