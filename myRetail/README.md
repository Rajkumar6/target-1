= myRetail RESTful service = 
myRetail is a rapidly growing company with HQ in Richmond, VA and over 200 stores across the east coast. myRetail wants to make its internal data available to any number of client devices, from myRetail.com to native mobile apps. 

The goal for this exercise is to create an end-to-end Proof-of-Concept for a products API, which will aggregate product data from multiple sources and return it as JSON to the caller. 

Your goal is to create a RESTful service that can retrieve product and price details by ID. The URL structure is up to you to define, but try to follow some sort of logical convention.

Build an application that performs the following actions: 

1. Responds to an HTTP GET request at /products/{id} and delivers product data as JSON (where {id} will be a number. 
  * Example product IDs: 15117729, 16483589, 16696652, 16752456, 15643793) 
  * Example response: {"id":13860428,"name":"The Big Lebowski (Blu-ray) (Widescreen)","current_price":{"value": 13.49,"currency_code":"USD"}}
2. Performs an HTTP GET to retrieve the product name from an external API. (For this exercise the data will come from api.target.com, but let’s just pretend this is an internal resource hosted by myRetail)    * Example: https://api.target.com/products/v3/13860428?fields=descriptions&id_type=TCIN&key=43cJWpLjH8Z8oR18KdrZDBKAgLLQKJjz
3. Reads pricing information from a NoSQL data store and combines it with the product id and name from the HTTP request into a single response.  
4. BONUS: Accepts an HTTP PUT request at the same path (/products/{id}), containing a JSON request body similar to the GET response, and updates the product’s price in the data store. 

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
1. apt-get install mongodb
1. sudo service mongodb start
1. mongo 

==== Create database ====
1. use myRetail
1. db.pricing.insert({"_id":13860428,"current_price":{"value": 13.49,"currency_code":"USD"}});

==== Add users ====
At this point, we’ll start by adding a global administrative user. Use the following command to select the administrative database:
```
db = db.getSiblingDB('admin')
```

Add a root user to this database:
```
db.addUser({user:"root", pwd:"temp",
    roles:[ "userAdminAnyDatabase", "readWrite" ] } )
```

Now that the administrative user has been created, it’s time to create the user that will be accessing the individual database. 
```
use myRetail
```

Then, create the user account:
```
db.addUser({user:"accountUser", pwd:"password", roles:[ "readWrite" ]})
```

Edit /etc/mongod.conf and add a line like this:
```
auth=true
```

==== Connecting to database ====
To test your Mongo configuration from another node (or to test authentication locally), run the command:

```
mongo localhost/admin -u root -p temp
```
to authenticate as root to make changes to the db structure or add users, or

```
mongo localhost/myRetail -u accountUser -p password
```
to authenticate as web_user.

==== Populate Database ====
We now need to initialize the database:
```
mongo localhost/myRetail -u accountUser -p password
use myRetail
db.prices.insert(
   {
     "_id":13860428,
     "current_price":{"value": 13.49,"currency_code":"USD"}
   }
);
```

== How to Compile ==
mvn clean package

== How to Run ==
mvn clean deploy
server is running on http://127.0.0.1:8081/api/products
