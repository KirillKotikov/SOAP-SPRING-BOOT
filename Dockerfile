FROM adoptopenjdk/openjdk11
WORKDIR /service
COPY SOAP-SPRING-BOOT-0.0.1-SNAPSHOT.jar /SOAP-SPRING-BOOT.jar
CMD ["java", "-jar", "/SOAP-SPRING-BOOT.jar"]