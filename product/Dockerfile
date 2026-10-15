FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml /app/pom.xml
COPY core /app/core
COPY api-gateway /app/api-gateway
COPY authService /app/authService
COPY cart-service /app/cart-service
COPY discovery-server /app/discovery-server
COPY notificationService /app/notificationService
COPY order /app/order
COPY payment /app/payment
COPY product /app/product
COPY saga /app/saga
RUN mvn -pl core clean install -DskipTests
RUN mvn -pl product clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/product/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]