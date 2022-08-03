plugins {
    java
}

group = "com.teamdev.intern"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("com.google.guava:guava-testlib:31.1-jre")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.slf4j:slf4j-api:1.7.36")
    testImplementation("org.slf4j:slf4j-log4j12:1.7.36")

    implementation(project(":meador_runtime"))
    implementation(project(":fsm"))
    implementation(project(":abstract_machine"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}