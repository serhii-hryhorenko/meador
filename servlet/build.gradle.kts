plugins {
    java
    war
}

group = "com.teamdev.intern"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")
    implementation(project(":meador"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}