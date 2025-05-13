plugins {
  id("java")
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
  // Local project dependency
  implementation(project(":coachify-entity"))

  // Spring Security & MongoDB
  implementation("org.springframework.boot:spring-boot-starter-security:3.2.5")
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.2.5")

  // JWT support (jjwt)
  implementation("io.jsonwebtoken:jjwt-api:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

  // Jakarta Servlet API (needed for filter and request/response classes)
  implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")

  // PostConstruct annotation
  implementation("javax.annotation:javax.annotation-api:1.3.2")

  // Jackson for Instant serialization
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")

  // Lombok
  compileOnly("org.projectlombok:lombok:1.18.30")
  annotationProcessor("org.projectlombok:lombok:1.18.30")

  // JUnit
  testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.withType<Test> {
  useJUnitPlatform()
}
