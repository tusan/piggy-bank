# piggy-bank

A java application, used as backend for the piggy-bank-frontend app.

To work with the application, choose your preferred ide and import the project as "gradle project".

To run the code directly from command line:
* open a terminal and move into the directory where you have checked out
* move into docker folder and run docker-compose up
* Run "./gradlew clean build bootRun" to run the application

To generate self signed key:
keytool -genkeypair -alias piggybank-test.com -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore piggybank-test.p12