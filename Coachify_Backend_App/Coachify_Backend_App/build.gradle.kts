plugins {
  id("org.springframework.boot") version "3.2.5"
  id("io.spring.dependency-management") version "1.1.4"
  java
}

group = "io.coachify"
version = "0.0.1-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

dependencies {
  // Core Spring Boot starter
  implementation("org.springframework.boot:spring-boot-starter")

  // Web support — includes Servlet API (HttpServletRequest, etc.)
  implementation("org.springframework.boot:spring-boot-starter-web")

  // MongoDB support
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

  // Spring Security support
  implementation("org.springframework.boot:spring-boot-starter-security")


  // JWT: API + impl + Jackson integration
  implementation("io.jsonwebtoken:jjwt-api:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

  // MinIO support
  implementation("io.minio:minio:8.5.7")

  // Lombok
  implementation("org.projectlombok:lombok:1.18.30")
  annotationProcessor("org.projectlombok:lombok:1.18.30")

  // Validation support
  implementation("org.springframework.boot:spring-boot-starter-validation")

  // Test dependencies
  testImplementation("org.springframework.boot:spring-boot-starter-test")
}
