FROM openjdk:11
COPY target/solomon-0.0.1-SNAPSHOT.jar solomon-server.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/solomon-server.jar"]