plugins {
    java
}

group = "com.teamdev.intern"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":fsm"))
    implementation(project(":meador_runtime"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("com.google.guava:guava-testlib:31.1-jre")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha7")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}