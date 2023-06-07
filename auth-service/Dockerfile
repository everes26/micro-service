# Sử dụng OpenJDK 17 làm base image
FROM openjdk:17-jdk-slim

RUN mkdir /Auth

# Đặt thư mục làm working directory trong container
WORKDIR /Auth

# Copy mã nguồn dự án vào thư mục gốc trong container
COPY . /Auth

# Cài đặt Maven
RUN apt-get update && apt-get install -y maven

# Build project Maven
RUN mvn clean package -DskipTests -DfinalName=auth-service

# Sử dụng lại base image OpenJDK 17-jdk-slim
FROM openjdk:17-jdk-slim

# Đặt thư mục làm working directory trong container
WORKDIR /Auth

# Copy file JAR từ stage trước (stage 0) vào thư mục /Status trong stage hiện tại
COPY --from=0 /Auth/target/auth-service-0.0.1-SNAPSHOT.jar .

# Expose cổng 8082 để ứng dụng Spring Boot chạy trên đó
EXPOSE 8082

# Chạy ứng dụng Spring Boot
CMD ["java", "-jar", "auth-service-0.0.1-SNAPSHOT.jar"]
