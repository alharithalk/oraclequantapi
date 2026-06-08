# ---- Build stage: compile the Spring Boot jar with Maven + JDK 17 ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q clean package -DskipTests \
    && find target -maxdepth 1 -name "*.jar" ! -name "*.original" -exec cp {} app.jar \;

# ---- Runtime stage: run the jar on a slim JRE 17 ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /build/app.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
