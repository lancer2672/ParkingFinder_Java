# Cấu trúc Dockerfile mới
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Copy file jar từ thư mục hiện tại vào image mới
COPY ParkingFinder-0.0.1-SNAPSHOT.jar /app/my-app.jar

ENTRYPOINT ["java", "-jar", "/app/my-app.jar"]
