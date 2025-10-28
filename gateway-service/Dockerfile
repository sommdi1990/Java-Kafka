## Build stage: Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy Maven project files (multi-module)
COPY pom.xml ./
COPY shared-lib/pom.xml shared-lib/pom.xml
COPY gateway-service/pom.xml gateway-service/pom.xml
COPY settings-docker.xml /root/.m2/settings.xml

# Copy sources
COPY shared-lib/src shared-lib/src
COPY gateway-service/src gateway-service/src

# Build: install parent POM, then shared-lib, then this module
RUN mvn -q -N -f pom.xml -Dexec.skip=true -DskipTests install \
    && mvn -q -DskipTests -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.connectionTimeout=60000 -Dmaven.wagon.http.readTimeout=600000 -f shared-lib/pom.xml install \
    && mvn -q -DskipTests -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.connectionTimeout=60000 -Dmaven.wagon.http.readTimeout=600000 -f gateway-service/pom.xml -am package

## Runtime stage: JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /workspace/gateway-service/target/gateway-service-*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","/app/app.jar"]
