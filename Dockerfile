FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY mcp-gateway-types/pom.xml mcp-gateway-types/
COPY mcp-gateway-domain/pom.xml mcp-gateway-domain/
COPY mcp-gateway-infrastructure/pom.xml mcp-gateway-infrastructure/
COPY mcp-gateway-case/pom.xml mcp-gateway-case/
COPY mcp-gateway-api/pom.xml mcp-gateway-api/
COPY mcp-gateway-trigger/pom.xml mcp-gateway-trigger/
COPY mcp-gateway-app/pom.xml mcp-gateway-app/
RUN mvn dependency:go-offline -B -q
COPY . .
RUN mvn package -DskipTests -pl mcp-gateway-app -am -B -q

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/mcp-gateway-app/target/*.jar app.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
