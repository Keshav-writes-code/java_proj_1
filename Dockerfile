FROM public.ecr.aws/docker/library/maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /usr/src/app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM public.ecr.aws/docker/library/eclipse-temurin:17-jre-focal

WORKDIR /usr/src/app

COPY --from=builder /usr/src/app/target/enterprise-financial-transaction-engine-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
