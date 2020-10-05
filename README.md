# piggy-bank

A java 11 application, used as backend for the piggy-bank-frontend app.

### IDE 
To work with the application, choose your preferred IDE and import the project as "gradle project".

### Gradle runner 
To run the code directly from command line:
* open a terminal and move into the directory where you have checked out
* move into docker folder and run docker-compose up
* Run "./gradlew clean build bootRun" to run the application

### Secret Key generation
To generate self signed key:
keytool -genkeypair -alias piggybank-test.com -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore piggybank-test.p12

### Open Api
This service use [openapi from swagger](https://swagger.io/), to use the service while it is running open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) on your browser.