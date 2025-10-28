## Build stage: Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy Maven project files (multi-module)
COPY pom.xml ./
COPY shared-lib/pom.xml shared-lib/pom.xml
COPY kafka-manager/pom.xml kafka-manager/pom.xml

# Copy sources
COPY shared-lib/src shared-lib/src
COPY kafka-manager/src kafka-manager/src

# Build: install parent POM, then shared-lib, then this module
RUN mvn -q -N -f pom.xml -Dexec.skip=true -DskipTests install \
    && mvn -q -DskipTests -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.connectionTimeout=60000 -Dmaven.wagon.http.readTimeout=600000 -f shared-lib/pom.xml install \
    && mvn -q -DskipTests -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.http.connectionTimeout=60000 -Dmaven.wagon.http.readTimeout=600000 -f kafka-manager/pom.xml -am package

# Copy Maven settings to use public Aliyun mirror for better access from restricted networks
COPY kafka-manager/settings-docker.xml /root/.m2/settings.xml

## Runtime stage: JRE
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /workspace/kafka-manager/target/kafka-manager-*.jar app.jar

EXPOSE 8085
ENTRYPOINT ["java","-jar","/app/app.jar"]
