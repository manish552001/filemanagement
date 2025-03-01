# Build stage
FROM maven:3.9.0-amazoncorretto-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV TZ=Asia/Kolkata
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]