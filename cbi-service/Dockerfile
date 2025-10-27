# Multi-stage build for GraalVM Native Image
FROM ghcr.io/graalvm/graalvm-community:21 AS graalvm

# Install native-image
RUN gu install native-image

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY shared-lib/pom.xml shared-lib/
COPY */pom.xml ./*/

# Copy source code
COPY shared-lib/src shared-lib/src
COPY */src ./*/src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create native image
RUN native-image -jar target/*.jar -H:+StaticExecutableWithDynamicLibC --no-fallback

# Final stage
FROM ubuntu:22.04

# Install required libraries
RUN apt-get update && apt-get install -y \
    libc6 \
    libstdc++6 \
    && rm -rf /var/lib/apt/lists/*

# Copy the native executable
COPY --from=graalvm /app/target/* /app/

# Set working directory
WORKDIR /app

# Expose port
EXPOSE 8081

# Run the application
CMD ["./cbi-service"]
