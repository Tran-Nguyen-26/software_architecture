
FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl

# Thư mục tạm
VOLUME /tmp

# Copy file jar vào container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Chạy ứng dụng
ENTRYPOINT ["java","-jar","/app.jar"]