# Sử dụng một image JDK chính thức từ Docker Hub
FROM openjdk:17-jdk-alpine

# Tạo thư mục /app trong container để chứa file jar của ứng dụng
WORKDIR /app

# Copy file jar từ máy host vào container
COPY build/libs/ParkingFinder-0.0.1-SNAPSHOT.jar /app/my-app.jar

# Chỉ định lệnh để chạy ứng dụng Java bên trong container
ENTRYPOINT ["java", "-jar", "/app/my-app.jar"]
