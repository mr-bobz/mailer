# Mailer Service (Email backend)

This service is created using **Spring Boot** Framework. Spring Boot makes it easy to create stand-alone, production-grade Spring based Applications that you can "just run".

https://projects.spring.io/spring-boot/

### Features
* Stand-alone Spring application
* Embeds Tomcat Java Web Server (no need to deploy WAR files)
* Automatically configures Spring whenever possible
* No code generation and no XML configuration

This project uses **Maven** for dependency management & build. 

https://maven.apache.org/

### How to Run
You can run the application using 
* >./mvnw spring-boot:run 
  
  OR 
* you can build the JAR file with 
    >./mvnw clean 
    
    >./mvnw package 
    
    NOTE: if you run into errors, try skipping tests 
    >mvn package -DskipTests
* then you can run the JAR file:
    >java -jar target/mailer-0.0.1-SNAPSHOT.jar

*NOTE*: Above commands assume you are in the *mailer* directory, where you cloned this.

Or you can install STS (Spring Tool Suite), open this project and right click com/springboot/mailer/MailServiceApp.java and Select 'Java Application' or 'Spring Boot App'

https://spring.io/tools/sts

Maven dependencies often get corrupted when dowloading from various repos online. Try the following command to clean up or delete respective jars and rebuild.
> mvn dependency:purge-local-repository



### How to consume the service

Use a tool like Postman or CURL and invoke as shown below

https://www.getpostman.com/

Method & URL 
>POST http://localhost:5000/mail/send

Headers 
>Content-Type: application/json

Body (Raw - JSON) 
```javascript
{
  "to": ["bobbyj79@gmail.com"],
  "subject": "Hello There",
  "text" : "Congratulations, you just sent an email!  You are truly awesome!"
}
```

CURL eg:
```
curl -H "Content-Type: application/json" -X POST -d '{"to": ["bobbyj79@gmail.com"],"subject": "Hello There","text" : "Congratulations, you just sent an email!  You are truly awesome!"}' http://localhost:5000/mail/send
```

### Valid arguments

Property      | Type            | Content           | Comments
------------  | -------------   | -------------     | -------------
to            | String          | Array of emails   | Min 1 required
cc            | String Array    | Array of emails   | Optional
bcc           | String Array    | Array of emails   | Optional
subject       | String          | Mail Subject      | Optional
text          | String          | Mail Content      | Required
providerName  | String          | SendGrid/MailGun  | Optional


### Additional Configuration
Please provide valid keys in 
>/mailer/src/main/resources/config.properties


