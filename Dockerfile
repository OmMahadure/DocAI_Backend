FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

RUN mvn -B dependency:go-offline

COPY src src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080
//

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS:-} -jar app.jar --server.port=${PORT:-8080}"]
