FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY . .

# Give execute permission to mvnw
RUN chmod +x mvnw

# Build the Spring Boot app
RUN ./mvnw clean package -DskipTests

# Copy the built jar to a fixed name
RUN cp target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
