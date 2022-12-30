# Soap Spring-Boot Application

---

### Used technologies:
- Java Programming Language (JDK 11), RabbitMq, Docker;
- Frameworks: Spring Boot, Spring Web Service, WSDL4J, Lombok, JDBC, AMQP, Mapstruct;
- Database: H2 (in memory).

---
### Description:
It is SOAP application, which receiving, handle, getting information from DB, convert and send messages to RabbitMq. 
Also created receiver from RabbitMq in this application.
---

### Instruction:
Local test:
  1) You can run this application. For it you need set spring.profiles.active as "test" in application.yml file.
  2) Sent HTTP POST method with Header content-type = text/xml. You can find example for request in src/test/resource/requests/NEW.xml.

Test with Docker:
1) Set spring.profiles.active as "prod" in application.yml file.
2) Build jar file with `mvn clean install` command.
3) Collect jar file (SOAP-SPRING-BOOT-0.0.1-SNAPSHOT.jar), Dockerfile and docker-compose.yml in any directory and create terminal in this directory.
4) Use `docker-compose build` command for building docker image, then use `docker-compose up -d` for run container with this application.
5) Dockerfile and docker-compose.yml files you can find in root directory of this project (SOAP-SPRING-BOOT).

