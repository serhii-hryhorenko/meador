plugins {
    java
}

group = "com.teamdev.intern"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":meador"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("com.sparkjava:spark-core:2.9.4");
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}