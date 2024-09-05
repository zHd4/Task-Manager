plugins {
	application
	checkstyle
	jacoco
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "zhd4.taskmanager"
version = "0.0.1-SNAPSHOT"

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

application {
	mainClass.set("zhd4.taskmanager.AppApplication")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-configuration-processor")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	implementation("org.postgresql:postgresql")
	implementation("net.datafaker:datafaker:2.0.2")
	implementation("org.instancio:instancio-junit:3.3.1")

	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	implementation("org.mapstruct:mapstruct:1.5.3.Final")
	testImplementation("org.projectlombok:lombok:1.18.28")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.3.Final")

	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
	testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	testImplementation(platform("org.junit:junit-bom:5.10.1"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")

	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")

	runtimeOnly("com.h2database:h2")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
	dependsOn(tasks.test)
}

tasks.jacocoTestReport {
	reports {
		xml.required = true
	}
}
