FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:25-jre
RUN addgroup --system javauser && \
    adduser --system --ingroup javauser javauser
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN chown -R javauser:javauser /app
USER javauser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]