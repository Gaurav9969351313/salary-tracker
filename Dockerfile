# Stage 1: Build stage
FROM maven AS build

# Install Node.js (LTS) and npm
# RUN apt-get update && apt-get install -y curl && \
#     curl -fsSL https://deb.nodesource.com/setup_lts.x | bash - && \
#     apt-get install -y nodejs && \
#     node -v && npm -v

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim AS final

WORKDIR /app
COPY --from=build /app/target/salary-tracker-svc-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
RUN rm -rf /app/src /app/.m2
