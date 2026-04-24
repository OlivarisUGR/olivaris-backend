# Build stage: compiles the Spring Boot application into an executable jar.
# Define the java image from ubuntu (jammy)
FROM eclipse-temurin:21-jdk-jammy AS builder

# Define the work folder where command will be executed
WORKDIR /workspace

# Maven needs his configuration (.mvn), the wrapper (mvnw) and dependencies which are on pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Download the maven dependencies and use the Docker cache (if dependencies does not change,
# it won't be used)
# DskipTests -> test won't be executed
RUN ./mvnw -q -DskipTests dependency:go-offline

COPY src/ src/

# Clean: removed target folder to avoid old objects
# Package: Compiled and packaged the app on a .jar file
# The result will be : ./target/*.jar
RUN ./mvnw -q -DskipTests clean package

# Runtime stage: runs only the generated jar with a smaller JRE image.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Create an spring user/group without root privileges. It is better to run the app as a normal 
# user than root user
RUN groupadd -r spring && useradd -r -g spring spring

# Copy the .jar file created into app.jar on docker
COPY --from=builder /workspace/target/*.jar /app/app.jar

# Change the owner file to spring
RUN chown spring:spring /app/app.jar

#Execute the .jar as spring user
USER spring
EXPOSE 8080

# Command that will be executed when container starts
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
