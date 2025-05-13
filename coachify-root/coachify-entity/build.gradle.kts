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
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.2.5")

  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.0")

  compileOnly("org.projectlombok:lombok:1.18.30")
  annotationProcessor("org.projectlombok:lombok:1.18.30")

  implementation("org.mongodb:mongodb-driver-core:4.11.1")
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.withType<Test> {
  useJUnitPlatform()
}
