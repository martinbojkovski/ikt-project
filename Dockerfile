FROM maven:3.9.5-eclipse-temurin-21 as build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk
COPY --from=build /target/ikt-project-0.0.1-SNAPSHOT.jar ikt-project.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "ikt-project.jar"]