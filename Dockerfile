## Sử dụng một image JDK chính thức từ Docker Hub
#FROM openjdk:17-jdk-alpine AS build
#
## Cài đặt các dependency cần thiết và Gradle
#ENV GRADLE_VERSION 8.0
#ENV GRADLE_HOME /opt/gradle
#
#RUN apk add --no-cache curl unzip \
#    && curl -L https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle-${GRADLE_VERSION}-bin.zip \
#    && unzip gradle-${GRADLE_VERSION}-bin.zip -d /opt \
#    && ln -s /opt/gradle-${GRADLE_VERSION} ${GRADLE_HOME} \
#    && rm gradle-${GRADLE_VERSION}-bin.zip
#
#ENV PATH=$PATH:$GRADLE_HOME/bin
#
## Thiết lập thư mục làm việc
#WORKDIR /app
#
## Copy toàn bộ mã nguồn vào container
#COPY . .
#
## Chạy lệnh build Gradle để tạo file JAR, bỏ qua quá trình kiểm thử
#RUN gradle build --no-daemon -x test
#
## Tạo một image mới chỉ chứa file jar đã được build
#FROM openjdk:17-jdk-alpine
#
## Tạo thư mục /app trong container để chứa file jar của ứng dụng
#WORKDIR /app
#
## Copy file jar từ giai đoạn build vào image mới
#COPY --from=build /app/build/libs/ParkingFinder-0.0.1-SNAPSHOT.jar /app/my-app.jar
#
## Chỉ định lệnh để chạy ứng dụng Java bên trong container
#ENTRYPOINT ["java", "-jar", "/app/my-app.jar"]
#
#

# Cấu trúc Dockerfile mới
FROM openjdk:17-jdk-alpine

WORKDIR /app

# Tạo thư mục uploads và đặt quyền
RUN mkdir -p /app/uploads && chmod 777 /app/uploads

# Copy file jar từ thư mục hiện tại vào image mới
COPY ParkingFinder-0.0.1-SNAPSHOT.jar /app/my-app.jar

# Đặt biến môi trường cho thư mục uploads
ENV FILE_UPLOAD_DIR=/app/uploads

ENTRYPOINT ["java", "-jar", "/app/my-app.jar"]
