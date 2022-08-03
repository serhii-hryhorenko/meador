plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(15))
    }
}

group = "com.teamdev.intern"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
    testImplementation("com.google.guava:guava-testlib:31.1-jre")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha7")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform()
}