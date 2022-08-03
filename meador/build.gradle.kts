plugins {
    java
}

group = "com.teamdev.intern"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
    testImplementation("com.google.guava:guava-testlib:31.1-jre")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha7")

    implementation(project(":meador_runtime"))
    implementation(project(":fsm"))
    implementation(project(":abstract_machine"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}